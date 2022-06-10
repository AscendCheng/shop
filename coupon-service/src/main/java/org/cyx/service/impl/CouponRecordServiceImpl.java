package org.cyx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.cyx.feign.ProductOrderFeignService;
import org.cyx.config.MqConfig;
import org.cyx.enums.*;
import org.cyx.exception.BizException;
import org.cyx.interceptor.LoginInterceptor;
import org.cyx.mapper.CouponRecordMapper;
import org.cyx.mapper.CouponTaskMapper;
import org.cyx.model.CouponRecordDO;
import org.cyx.model.CouponRecordMessage;
import org.cyx.model.CouponTaskDO;
import org.cyx.model.LoginUser;
import org.cyx.request.LockCouponRecordRequest;
import org.cyx.service.CouponRecordService;
import org.cyx.util.JsonData;
import org.cyx.vo.CouponRecordVo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author cyx
 * @since 2021-03-04
 */
@Service
@Slf4j
public class CouponRecordServiceImpl extends ServiceImpl<CouponRecordMapper, CouponRecordDO> implements CouponRecordService {
    @Autowired
    private CouponRecordMapper couponRecordMapper;

    @Autowired
    private CouponTaskMapper couponTaskMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MqConfig mqConfig;

    @Autowired
    private ProductOrderFeignService productOrderFeignService;

    @Override
    public Map<String, Object> page(int page, int size) {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        Page<CouponRecordDO> pageInfo = new Page<>(page, size);
        IPage<CouponRecordDO> couponRecordDOPage =
                couponRecordMapper.selectPage(pageInfo, new QueryWrapper<CouponRecordDO>()
                        .eq("user_id", loginUser.getId()).orderByDesc("create_time"));
        Map<String, Object> pageMap = new HashMap<>();
        pageMap.put("total_record", couponRecordDOPage.getTotal());
        pageMap.put("total_page", couponRecordDOPage.getPages());
        pageMap.put("current_data", couponRecordDOPage.getRecords().stream()
                .map(couponRecordDO -> beanProcess(couponRecordDO)).collect(Collectors.toList()));
        return pageMap;
    }

    @Override
    public CouponRecordVo findById(Long recordId) {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        CouponRecordDO couponRecordDO = couponRecordMapper.selectOne(
                new QueryWrapper<CouponRecordDO>().eq("id", recordId).eq("user_id", loginUser.getId()));
        if (couponRecordDO == null) {
            return null;
        }
        return beanProcess(couponRecordDO);
    }

    @Override
    public JsonData lockCouponRecords(LockCouponRecordRequest lockCouponRecordRequest) {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        String orderOutTradeNo = lockCouponRecordRequest.getOrderOutTradeNo();
        List<Long> lockCouponRecordIds = lockCouponRecordRequest.getLockCouponRecordIds();
        int updateRows = couponRecordMapper.lockUseStateBatch(loginUser.getId(), CouponStateEnum.USED.name(), lockCouponRecordIds);
        List<CouponTaskDO> couponTaskDOList = lockCouponRecordIds.stream().map(couponRecordId -> {
            CouponTaskDO couponTaskDO = new CouponTaskDO();
            couponTaskDO.setCreateTime(new Date());
            couponTaskDO.setOutTradeNo(orderOutTradeNo);
            couponTaskDO.setCouponRecordId(couponRecordId);
            couponTaskDO.setLockState(StockTaskStateEnum.LOCK.name());
            return couponTaskDO;
        }).collect(Collectors.toList());
        log.info("优惠券锁定:{}", updateRows);
        int insertRows = couponTaskMapper.insertBatch(couponTaskDOList);
        log.info("新增优惠券记录:{}", insertRows);
        if (lockCouponRecordIds.size() == updateRows && updateRows == insertRows) {
            for (CouponTaskDO couponTaskDO : couponTaskDOList) {
                CouponRecordMessage couponRecordMessage = new CouponRecordMessage();
                couponRecordMessage.setTaskId(couponTaskDO.getId());
                couponRecordMessage.setOutTradeNo(orderOutTradeNo);
                rabbitTemplate.convertAndSend(mqConfig.getEventExchange(), mqConfig.getCouponReleaseDelayRoutingKey(), couponRecordMessage);
                log.info("优惠券锁定消息记录:{}", couponRecordMessage.toString());
            }
            return JsonData.buildSuccess();
        } else {
            throw new BizException(BizCodeEnum.COUPON_RECORD_LOCK_FAIL);
        }
    }

    /**
     * 解锁优惠券记录
     * 1）查询task工作单是否存在
     * 2）查询订单状态
     */
    @Override
    public boolean releaseCouponRecord(CouponRecordMessage couponRecordMessage) {
        // 查询task是否存在
        CouponTaskDO couponTaskDO = couponTaskMapper.selectOne(new QueryWrapper<CouponTaskDO>().eq("id", couponRecordMessage.getTaskId()));
        if (couponTaskDO == null) {
            log.warn("工作单不存在:{}", couponRecordMessage);
            return true;
        }
        if (couponTaskDO.getLockState().equalsIgnoreCase(StockTaskStateEnum.LOCK.name())) {
            JsonData jsonData = productOrderFeignService.queryProductOrderState(couponRecordMessage.getOutTradeNo());
            if (jsonData.getCode() == 0) {
                // 判断订单状态
                String state = jsonData.getData().toString();
                if (ProductOrderStateEnum.NEW.name().equals(state)) {
                    // 状态是new状态
                    log.warn("订单状态是NEW，返回给消息队列，重新投递:{}", couponRecordMessage);
                    return false;
                }
                // 如果已支付
                if (ProductOrderStateEnum.PAY.name().equalsIgnoreCase(state)) {
                    // 如果已支付，修改task状态为finish
                    couponTaskDO.setLockState(StockTaskStateEnum.FINISH.name());
                    couponTaskMapper.update(couponTaskDO, new QueryWrapper<CouponTaskDO>().eq("id", couponRecordMessage.getTaskId()));
                    log.info("订单已支付，需改库存锁定工作单FINISH状态:{}", couponRecordMessage);
                }
            }

            log.warn("订单不存在，或者订单被取消，确认消息，修改task状态为CANCEL，恢复优惠券记录为NEW,message:{}", couponRecordMessage);
            couponTaskDO.setLockState(StockTaskStateEnum.CANCEL.name());
            couponTaskMapper.update(couponTaskDO, new QueryWrapper<CouponTaskDO>().eq("id", couponRecordMessage.getTaskId()));

            // 恢复优惠券记录
            couponRecordMapper.updateState(couponTaskDO.getCouponRecordId(), CouponStateEnum.NEW.name());
            return true;
        } else {
            log.warn("工作单状态不是LOCK,state={},消息体:{}", couponTaskDO.getLockState(), couponRecordMessage);
            return true;
        }
    }

    private CouponRecordVo beanProcess(CouponRecordDO couponRecordDO) {
        CouponRecordVo couponRecordVo = new CouponRecordVo();
        BeanUtils.copyProperties(couponRecordDO, couponRecordVo);
        return couponRecordVo;
    }
}
