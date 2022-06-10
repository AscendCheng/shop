package org.cyx.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.cyx.componet.PayFactory;
import org.cyx.config.MqConfig;
import org.cyx.constant.CacheKey;
import org.cyx.enums.BizCodeEnum;
import org.cyx.enums.ProductOrderPayTypeEnum;
import org.cyx.enums.ProductOrderStateEnum;
import org.cyx.enums.ProductOrderTypeEnum;
import org.cyx.exception.BizException;
import org.cyx.feign.CouponFeignService;
import org.cyx.feign.ProductFeignService;
import org.cyx.feign.UserFeignService;
import org.cyx.interceptor.LoginInterceptor;
import org.cyx.mapper.ProductOrderItemMapper;
import org.cyx.mapper.ProductOrderMapper;
import org.cyx.model.LoginUser;
import org.cyx.model.OrderMessage;
import org.cyx.model.ProductOrderDO;
import org.cyx.model.ProductOrderItemDO;
import org.cyx.request.ConfirmOrderRequest;
import org.cyx.request.LockCouponRecordRequest;
import org.cyx.request.LockProductRequest;
import org.cyx.request.RepayOrderRequest;
import org.cyx.service.ProductOrderService;
import org.cyx.util.CommonUtil;
import org.cyx.util.JsonData;
import org.cyx.vo.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author cyx
 * @since 2021-04-15
 */
@Service
@Slf4j
public class ProductOrderServiceImpl extends ServiceImpl<ProductOrderMapper, ProductOrderDO> implements ProductOrderService {
    private static final long ORDER_PAY_TIME_OUT_MILLS = 5 * 60 * 1000;

    @Autowired
    private ProductOrderMapper productOrderMapper;

    @Autowired
    private UserFeignService userFeignService;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private ProductOrderItemMapper productOrderItemMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MqConfig mqConfig;

    @Autowired
    private PayFactory payFactory;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    @Transactional
    public JsonData confirmOrder(ConfirmOrderRequest confirmOrderRequest) {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();

        String token = confirmOrderRequest.getToken();
        if (StringUtils.isBlank(token)) {
            return JsonData.buildResult(BizCodeEnum.ORDER_CONFIRM_TOKEN_EQUAL_FAIL);
        }

        String script = "if redis.call('get',KEYS[1])== ARGV[1] then return redis.call('del',KEY[1]) else return 0 end";
        Long result = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList(String.format(CacheKey.SUBMIT_ORDER_TOKEN_KEY, loginUser.getId())), token);
        if (result == 0L) {
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_TOKEN_EQUAL_FAIL);
        }

        String outTradeNo = CommonUtil.getRandomString(32);
        ProductOrderAddressVo productOrderAddressVo = this.getAddress(confirmOrderRequest.getAddressId());
        log.info("address:", productOrderAddressVo.toString());

        // 获取用户放入购物车的商品
        List<Long> productList = confirmOrderRequest.getProductIds();
        JsonData cartItemData = productFeignService.confirmOrderCartItem(productList);
        List<OrderItemVo> orderItemVoList = cartItemData.getData(new TypeReference<List<OrderItemVo>>() {
        });
        log.info("获取的商品", orderItemVoList);
        if (orderItemVoList == null) {
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_CART_ITEM_NOT_EXIST);
        }
        // 验证价格，减去商品优惠券
        this.checkPrice(orderItemVoList, confirmOrderRequest);
        // 锁定优惠券
        this.lockCouponRecords(confirmOrderRequest, outTradeNo);
        // 锁定商品
        this.lockProductStock(orderItemVoList, outTradeNo);
        // 创建订单
        ProductOrderDO productOrderDO = this.saveProductOrder(confirmOrderRequest, loginUser, outTradeNo, productOrderAddressVo);
        // 新增订单项
        saveProductOrderItems(outTradeNo, productOrderDO.getId(), orderItemVoList);
        // 发送延迟消息，用于自动关单
        OrderMessage orderMessage = new OrderMessage();
        orderMessage.setOutTradeNo(outTradeNo);
        rabbitTemplate.convertAndSend(mqConfig.getOrderEventExchange(), mqConfig.getOrderCloseDelayRoutingKey(), orderMessage);

        // 创建支付
        PayInfoVo payInfoVo = new PayInfoVo(outTradeNo, productOrderDO.getPayAmount(),
                confirmOrderRequest.getPayType(), confirmOrderRequest.getClientType(),
                outTradeNo, "", ORDER_PAY_TIME_OUT_MILLS);
        String payResult = payFactory.pay(payInfoVo);
        if (StringUtils.isNotBlank(payResult)) {
            log.info("创建支付订单成功,payInfo:{},payResult", payInfoVo);
            return JsonData.buildSuccess(payResult);
        } else {
            log.error("创建支付订单失败,payInfo:{},payResult", payInfoVo);
            return JsonData.buildResult(BizCodeEnum.PAY_ORDER_FAIL);
        }
    }

    private void saveProductOrderItems(String outTradeNo, Long orderId, List<OrderItemVo> orderItemVoList) {
        List<ProductOrderItemDO> list = orderItemVoList.stream().map(obj -> {
            ProductOrderItemDO item = new ProductOrderItemDO();
            item.setBuyNum(obj.getBuyNum());
            item.setProductId(obj.getProductId());
            item.setProductName(obj.getProductTitle());
            item.setProductImg(obj.getProductImg());
            item.setOutTradeNo(outTradeNo);
            item.setCreateTime(new Date());
            item.setAmount(obj.getAmount());
            item.setTotalAmount(obj.getTotalAmount());
            item.setProductOrderId(orderId);
            return item;
        }).collect(Collectors.toList());

        productOrderItemMapper.insertAll(list);
    }

    private void lockProductStock(List<OrderItemVo> orderItemVoList, String outTradeNo) {
        List<OrderItemVo> itemRequestList = orderItemVoList.stream().map(obj -> {
            OrderItemVo orderItemVo = new OrderItemVo();
            orderItemVo.setBuyNum(obj.getBuyNum());
            orderItemVo.setProductId(obj.getProductId());
            return orderItemVo;
        }).collect(Collectors.toList());
        LockProductRequest lockProductRequest = new LockProductRequest();
        lockProductRequest.setOrderOutTradeNo(outTradeNo);
        lockProductRequest.setOrderItemList(itemRequestList);
        JsonData jsonData = productFeignService.lockProductStock(lockProductRequest);
        if (jsonData.isFail()) {
            log.error("锁定商品库存失败：{}", lockProductRequest);
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_LOCK_PRODUCT_FAIL);
        }
    }

    private void lockCouponRecords(ConfirmOrderRequest confirmOrderRequest, String outTradeNo) {
        List<Long> lockCouponRecordIds = new ArrayList<>();
        if (confirmOrderRequest.getCouponRecordId() > 0) {
            lockCouponRecordIds.add(confirmOrderRequest.getCouponRecordId());
            LockCouponRecordRequest lockCouponRecordRequest = new LockCouponRecordRequest();
            lockCouponRecordRequest.setOrderOutTradeNo(outTradeNo);
            lockCouponRecordRequest.setLockCouponRecordIds(lockCouponRecordIds);

            JsonData jsonData = couponFeignService.lockCouponRecords(lockCouponRecordRequest);
            if (jsonData.isFail()) {
                throw new BizException(BizCodeEnum.COUPON_RECORD_LOCK_FAIL);
            }
        }
    }

    private void checkPrice(List<OrderItemVo> orderItemVoList, ConfirmOrderRequest orderRequest) {
        BigDecimal realPayAmount = BigDecimal.ZERO;
        if (orderItemVoList != null) {
            for (OrderItemVo orderItemVo : orderItemVoList) {
                BigDecimal itemRealPayAmount = orderItemVo.getTotalAmount();
                realPayAmount = realPayAmount.add(itemRealPayAmount);
            }
        }

        // 获取优惠券
        CouponRecordVo couponRecordVo = getCouponRecord(orderRequest.getCouponRecordId());
        if (couponRecordVo != null) {
            if (realPayAmount.compareTo(couponRecordVo.getConditionPrice()) < 0) {
                throw new BizException(BizCodeEnum.ORDER_CONFIRM_COUPON_FAIL);
            }
            if (couponRecordVo.getPrice().compareTo(realPayAmount) > 0) {
                realPayAmount = BigDecimal.ZERO;
            } else {
                realPayAmount = realPayAmount.subtract(couponRecordVo.getPrice());
            }
        }
        if (realPayAmount.compareTo(orderRequest.getRealPayAmount()) != 0) {
            log.error("订单验价失败", BizCodeEnum.ORDER_CONFIRM_COUPON_FAIL);
        }
    }

    private CouponRecordVo getCouponRecord(long id) {
        if (id < 0) {
            return null;
        }
        JsonData couponData = couponFeignService.findUserCouponRecordById(id);
        if (couponData.isFail()) {
            throw new BizException(BizCodeEnum.COUPON_NO_EXITS);
        }
        CouponRecordVo couponRecordVo = couponData.getData(new TypeReference<CouponRecordVo>() {
        });

        long currentTime = System.currentTimeMillis();
        long startTime = couponRecordVo.getStartTime().getTime();
        long endTime = couponRecordVo.getEndTime().getTime();

        if (currentTime > startTime && currentTime < endTime) {
            return couponRecordVo;
        }
        return null;
    }

    private ProductOrderAddressVo getAddress(Long addressId) {
        JsonData detailResult = userFeignService.getDetail(addressId);
        if (detailResult.isFail()) {
            log.error("查询失败");
            throw new BizException(BizCodeEnum.ADDRESS_NO_EXITS);
        }
        ProductOrderAddressVo productOrderAddressVo = detailResult.getData(new TypeReference<ProductOrderAddressVo>() {
        });
        return productOrderAddressVo;
    }

    private ProductOrderDO saveProductOrder(ConfirmOrderRequest orderRequest, LoginUser loginUser, String orderOutTradeNo, ProductOrderAddressVo addressVo) {
        ProductOrderDO productOrderDO = new ProductOrderDO();
        productOrderDO.setUserId(loginUser.getId());
        productOrderDO.setHeadImg(loginUser.getHeadImg());
        productOrderDO.setNickname(loginUser.getName());
        productOrderDO.setOutTradeNo(orderOutTradeNo);
        productOrderDO.setCreateTime(new Date());
        productOrderDO.setDel(0);
        productOrderDO.setOrderType(ProductOrderTypeEnum.DAILY.name());
        productOrderDO.setPayAmount(orderRequest.getRealPayAmount());
        productOrderDO.setTotalAmount(orderRequest.getTotalAmount());
        productOrderDO.setState(ProductOrderStateEnum.NEW.name());
        ProductOrderPayTypeEnum payTypeEnum = ProductOrderPayTypeEnum.valueOf(orderRequest.getPayType());
        productOrderDO.setPayType(payTypeEnum != null ? payTypeEnum.name() : null);
        productOrderDO.setReceiverAddress(JSONObject.toJSONString(addressVo));
        productOrderMapper.insert(productOrderDO);
        return productOrderDO;
    }

    @Override
    public String queryProductState(String outTradeNo) {
        ProductOrderDO productOrderDO = productOrderMapper.selectOne(new QueryWrapper<ProductOrderDO>().eq("out_trade_no", outTradeNo));
        return productOrderDO == null ? "" : productOrderDO.getState();
    }

    @Override
    public boolean closeProductOrder(OrderMessage orderMessage) {
        ProductOrderDO productOrderDO = productOrderMapper.selectOne(new QueryWrapper<ProductOrderDO>()
                .eq("out_trade_no", orderMessage.getOutTradeNo()));
        if (productOrderDO == null) {
            // 订单不存在
            log.warn("直接确认消息，订单不存在：{}", orderMessage);
            return true;
        }
        if (productOrderDO.getState().equalsIgnoreCase(ProductOrderStateEnum.PAY.name())) {
            // 已经支付
            log.warn("直接确认消息，订单已支付：{}", orderMessage);
            return true;
        }

        // 向第三方查询是否已支付
        PayInfoVo payInfoVo = new PayInfoVo();
        payInfoVo.setOutTradeNo(orderMessage.getOutTradeNo());
        payInfoVo.setPayType(productOrderDO.getPayType());
        String payResult = payFactory.queryPaySuccess(payInfoVo);

        // 结果为空，则未支付成功，本地取消订单
        if (StringUtils.isBlank(payResult)) {
            productOrderMapper.updateOrderPayState(productOrderDO.getOutTradeNo(), ProductOrderStateEnum.CANCEL.name(), ProductOrderStateEnum.NEW.name());
            log.info("结果为空，则未支付成功，本地取消订单：{}", orderMessage);
            return true;
        } else {
            log.warn("支付成功，主动的把订单状态改成已支付，造成该原因的情况可能是是支付通道回到有问题：{}", orderMessage);
            productOrderMapper.updateOrderPayState(productOrderDO.getOutTradeNo(), ProductOrderStateEnum.PAY.name(), ProductOrderStateEnum.NEW.name());
            return true;
        }
    }

    @Override
    public JsonData handlerOrderCallbackMsg(ProductOrderPayTypeEnum payTypeEnum, Map<String, String> params) {
        if (payTypeEnum == ProductOrderPayTypeEnum.ALIPAY) {
            // 订单号
            String outTradeNo = params.get("out_trade_no");
            // 订单状态
            String tradeStatus = params.get("trade_status");
            if ("TRADE_SUCCESS".equalsIgnoreCase(tradeStatus) || "TRADE_FINISHED".equalsIgnoreCase(tradeStatus)) {
                // 更新订单状态
                productOrderMapper.updateOrderPayState(outTradeNo, ProductOrderStateEnum.PAY.name(), ProductOrderStateEnum.NEW.name());
                return JsonData.buildSuccess();
            }
        }
        return JsonData.buildResult(BizCodeEnum.PAY_ORDER_CALLBACK_NOT_SUCCESS);
    }

    @Override
    @Transactional
    public Map<String, Object> listOrder(int page, int size, String state) {
        Page<ProductOrderDO> pageInfo = new Page<>(page, size);
        IPage<ProductOrderDO> productDOIPage = productOrderMapper.selectPage(pageInfo, new QueryWrapper<ProductOrderDO>().eq("state", state).orderByDesc("create_time"));
        List<ProductOrderVo> productOrderVoList = productDOIPage.getRecords().stream().map(orderVo -> {
            ProductOrderVo productOrderVo = new ProductOrderVo();
            BeanUtils.copyProperties(orderVo, productOrderVo);
            List<ProductOrderItemDO> orderItemDOList = productOrderItemMapper.selectList(new QueryWrapper<ProductOrderItemDO>().eq("product_order_id", orderVo.getId()));
            List<OrderItemVo> itemList = orderItemDOList.stream().map(productOrderItemDO -> {
                OrderItemVo orderItemVo = new OrderItemVo();
                BeanUtils.copyProperties(productOrderItemDO, orderItemVo);
                return orderItemVo;
            }).collect(Collectors.toList());
            productOrderVo.setItemList(itemList);
            return productOrderVo;
        }).collect(Collectors.toList());
        Map<String, Object> pageMap = new HashMap<>(3);
        pageMap.put("total_record", productDOIPage.getTotal());
        pageMap.put("total_page", productDOIPage.getPages());
        pageMap.put("current_data", productOrderVoList);
        return pageMap;
    }

    @Override
    public JsonData repayOrder(RepayOrderRequest repayOrderRequest) {
        LoginUser user = LoginInterceptor.threadLocal.get();
        ProductOrderDO productOrderDO = productOrderMapper.selectOne(new QueryWrapper<ProductOrderDO>().eq("out_trade_no", repayOrderRequest.getOutTradeNo()).eq("user_id", user.getId()));
        if (productOrderDO == null) {
            return JsonData.buildResult(BizCodeEnum.PAY_ORDER_NOT_EXIST);
        }
        log.info("订单{}状态：{}", productOrderDO.getOutTradeNo(), productOrderDO.getState());
        if (!ProductOrderStateEnum.NEW.name().equalsIgnoreCase(productOrderDO.getOrderType())) {
            return JsonData.buildResult(BizCodeEnum.PAY_ORDER_STATE_ERROR);
        } else {
            long orderLiveTime = CommonUtil.getCurrentTimeStamp() - productOrderDO.getCreateTime().getTime();
            // 增加70秒防止在关闭前提交
            orderLiveTime = orderLiveTime + 70 * 1000;
            if (orderLiveTime > ORDER_PAY_TIME_OUT_MILLS) {
                return JsonData.buildResult(BizCodeEnum.PAY_ORDER_PAY_TIMEOUT);
            } else {
                // 还有可以通过异步更新支付信息，如payType
                long timeout = ORDER_PAY_TIME_OUT_MILLS - orderLiveTime;
                PayInfoVo payInfoVo = new PayInfoVo(repayOrderRequest.getOutTradeNo(), productOrderDO.getPayAmount(),
                        repayOrderRequest.getPayType(), repayOrderRequest.getClientType(),
                        repayOrderRequest.getOutTradeNo(), "", timeout);
                log.info("payInfoVo={}", payInfoVo);
                String payResult = payFactory.pay(payInfoVo);
                if (StringUtils.isNotBlank(payResult)) {
                    log.info("二次创建支付订单成功,payInfo:{},payResult", payInfoVo);
                    return JsonData.buildSuccess(payResult);
                } else {
                    log.error("二次创建支付订单失败,payInfo:{},payResult", payInfoVo);
                    return JsonData.buildResult(BizCodeEnum.PAY_ORDER_FAIL);
                }
            }

        }
    }
}
