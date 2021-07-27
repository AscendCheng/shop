package org.cyx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.cyx.config.MqConfig;
import org.cyx.enums.BizCodeEnum;
import org.cyx.enums.ProductOrderStateEnum;
import org.cyx.enums.StockTaskStateEnum;
import org.cyx.exception.BizException;
import org.cyx.feign.ProductOrderFeignService;
import org.cyx.mapper.ProductMapper;
import org.cyx.mapper.ProductTaskMapper;
import org.cyx.model.ProductDO;
import org.cyx.model.ProductMessage;
import org.cyx.model.ProductTaskDO;
import org.cyx.request.LockProductRequest;
import org.cyx.request.OrderItemRequest;
import org.cyx.service.ProductService;
import org.cyx.util.JsonData;
import org.cyx.vo.ProductVO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author cyx
 * @since 2021-04-07
 */
@Service
@Slf4j
public class ProductServiceImpl extends ServiceImpl<ProductMapper, ProductDO> implements ProductService {
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductTaskMapper productTaskMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MqConfig mqConfig;

    @Autowired
    private ProductOrderFeignService productOrderFeignService;

    @Override
    public Map<String, Object> page(int page, int size) {
        Page<ProductDO> pageInfo = new Page<>(page, size);
        IPage<ProductDO> productDOIPage = productMapper.selectPage(pageInfo, null);
        Map<String, Object> pageMap = new HashMap<>(3);
        pageMap.put("total_record", productDOIPage.getTotal());
        pageMap.put("total_page", productDOIPage.getPages());
        pageMap.put("current_data", productDOIPage.getRecords()
                .stream().map(this::beanProcess).collect(Collectors.toList()));
        return pageMap;
    }

    @Override
    public ProductVO findDetailById(String id) {
        ProductDO productDO = productMapper.selectById(id);
        return beanProcess(productDO);
    }

    @Override
    public List<ProductVO> findDetailByIdBatch(List<Long> productIdList) {
        List<ProductDO> productDOList = productMapper.selectList(new QueryWrapper<ProductDO>().in("id", productIdList));
        return productDOList.stream().map(this::beanProcess).collect(Collectors.toList());
    }

    /**
     * 锁定商品库存
     * <p>
     * 1）遍历商品，锁定每个商品购买数量
     * 2）每一次锁定的时候，都要发送延迟消息
     *
     * @param lockProductRequest
     * @return
     */
    @Override
    public JsonData lockProduct(LockProductRequest lockProductRequest) {
        String outTradeNo = lockProductRequest.getOrderOutTradeNo();
        List<OrderItemRequest> orderItemRequestList = lockProductRequest.getOrderItemList();
        List<Long> productIdList = orderItemRequestList.stream().map(OrderItemRequest::getProductId).collect(Collectors.toList());
        List<ProductVO> productVOList = this.findDetailByIdBatch(productIdList);
        Map<Long, ProductVO> productVOMap = productVOList.stream().collect(Collectors.toMap(ProductVO::getId, Function.identity(), (k1, k2) -> k1));
        for (OrderItemRequest orderItemRequest : orderItemRequestList) {
            if (productVOMap.containsKey(orderItemRequest.getProductId())) {
                int rows = productMapper.lockProductStock(orderItemRequest.getProductId(), orderItemRequest.getBuyNum());
                if (rows == 0) {
                    throw new BizException(BizCodeEnum.ORDER_CONFIRM_LOCK_PRODUCT_FAIL);
                } else {
                    ProductVO productVO = productVOMap.get(orderItemRequest.getProductId());
                    ProductTaskDO productTaskDO = new ProductTaskDO();
                    productTaskDO.setBuyNum(orderItemRequest.getBuyNum());
                    productTaskDO.setLockState(StockTaskStateEnum.LOCK.name());
                    productTaskDO.setProductId(orderItemRequest.getProductId());
                    productTaskDO.setProductName(productVO.getTitle());
                    productTaskDO.setOutTradeNo(outTradeNo);
                    productTaskMapper.insert(productTaskDO);
                    // 发送MQ延迟消息
                    ProductMessage productMessage = new ProductMessage();
                    productMessage.setOutTradeNo(outTradeNo);
                    productMessage.setTaskId(productTaskDO.getId());

                    rabbitTemplate.convertAndSend(mqConfig.getEventExchange(), mqConfig.getStockReleaseDelayRoutingKey(), productMessage);
                    log.info("商品库存锁定信息延迟消息发送成功:{}", productMessage);
                }
            }
        }
        return JsonData.buildSuccess();
    }

    @Override
    public boolean releaseProductStock(ProductMessage productMessage) {
        ProductTaskDO productTaskDO = productTaskMapper.selectById(productMessage.getTaskId());
        if (productTaskDO == null) {
            log.warn("工作单不存在，消息体为：{}", productMessage);
        }

        // lock状态才能处理
        if (productTaskDO.getLockState().equalsIgnoreCase(StockTaskStateEnum.LOCK.name())) {
            JsonData jsonData = productOrderFeignService.queryProductOrderState(productMessage.getOutTradeNo());
            if (jsonData.getCode() == 0) {
                // 判断订单状态
                String state = jsonData.getData().toString();
                if (ProductOrderStateEnum.NEW.name().equals(state)) {
                    // 状态是new状态
                    log.warn("订单状态是NEW，返回给消息队列，重新投递:{}", productMessage);
                    return false;
                }
                // 如果已支付
                if (ProductOrderStateEnum.PAY.name().equalsIgnoreCase(state)) {
                    // 如果已支付，修改task状态为finish
                    productTaskDO.setLockState(StockTaskStateEnum.FINISH.name());
                    productTaskMapper.update(productTaskDO, new QueryWrapper<ProductTaskDO>().eq("id", productMessage.getTaskId()));
                    log.info("订单已支付，需改库存锁定工作单FINISH状态:{}", productMessage);
                }
            }

            log.warn("订单不存在，或者订单被取消，确认消息，修改task状态为CANCEL，恢复商品库存,message:{}", productMessage);
            productTaskDO.setLockState(StockTaskStateEnum.CANCEL.name());
            productTaskMapper.update(productTaskDO, new QueryWrapper<ProductTaskDO>().eq("id", productMessage.getTaskId()));

            // 恢复优惠券记录
            productMapper.unlockProductStock(productTaskDO.getProductId(), productTaskDO.getBuyNum());
            return true;
        } else {
            log.warn("工作单状态不是LOCK,state={},消息体:{}", productTaskDO.getLockState(), productMessage);
            return true;
        }
    }

    private ProductVO beanProcess(ProductDO productDO) {
        if (productDO == null) {
            return null;
        }
        ProductVO productVO = new ProductVO();
        BeanUtils.copyProperties(productDO, productVO);
        return productVO;
    }
}
