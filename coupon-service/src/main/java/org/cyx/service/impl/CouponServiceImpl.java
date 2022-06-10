package org.cyx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.cyx.enums.BizCodeEnum;
import org.cyx.enums.CouponCategoryEnum;
import org.cyx.enums.CouponStateEnum;
import org.cyx.enums.CouponStatusEnum;
import org.cyx.exception.BizException;
import org.cyx.interceptor.LoginInterceptor;
import org.cyx.mapper.CouponMapper;
import org.cyx.mapper.CouponRecordMapper;
import org.cyx.model.CouponDO;
import org.cyx.model.CouponRecordDO;
import org.cyx.model.LoginUser;
import org.cyx.request.NewUserCouponRequest;
import org.cyx.service.CouponService;
import org.cyx.util.CommonUtil;
import org.cyx.util.JsonData;
import org.cyx.vo.CouponVo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author cyx
 * @since 2021-03-04
 */
@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, CouponDO> implements CouponService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CouponServiceImpl.class);

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private CouponRecordMapper couponRecordMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public JsonData pageCouponActivity(int page, int size) {
        Page<CouponDO> couponDOPage = new Page<>(page, size);
        IPage<CouponDO> iPage = couponMapper.selectPage(couponDOPage,
                new QueryWrapper<CouponDO>()
                        .eq("publish", CouponStatusEnum.PUBLISH)
                        .eq("category", CouponCategoryEnum.PROMOTION)
                        .orderByDesc("create_time"));
        return JsonData.buildSuccess(CommonUtil.iPage2Map(iPage, do2Vo(iPage.getRecords())));
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public JsonData addCouponService(long couponId, CouponCategoryEnum category) {
        String lockKey = "lock:coupon:" + couponId + ":" + LoginInterceptor.threadLocal.get().getId();
        RLock rLock = redissonClient.getLock(lockKey);
        rLock.lock();
        LOGGER.info("领券接口加锁成功：{}", Thread.currentThread().getId());
        try {
            // 执行业务逻辑
            CouponDO checkExist = couponMapper.selectOne(new QueryWrapper<CouponDO>().eq("id", couponId)
                    .eq("category", category.name())
                    .eq("publish", CouponStatusEnum.PUBLISH));
            if (checkExist == null) {
                throw new BizException(BizCodeEnum.COUPON_NO_EXITS);
            }
            LoginUser user = LoginInterceptor.threadLocal.get();
            // 改优惠券是否可以领取
            checkCoupon(checkExist, user.getId());

            // 构建领券记录
            CouponRecordDO couponRecordDO = new CouponRecordDO();
            BeanUtils.copyProperties(checkExist, couponRecordDO);
            couponRecordDO.setCreateTime(new Date());
            couponRecordDO.setUseState(CouponStateEnum.NEW.name());
            couponRecordDO.setUserId(user.getId());
            couponRecordDO.setUserName(user.getName());
            couponRecordDO.setCouponId(couponId);
            couponRecordDO.setId(null);

            // 扣减库存
            int rows = couponMapper.reduceStock(couponId);
            if (rows > 0) {
                couponRecordMapper.insert(couponRecordDO);
            } else {
                LOGGER.warn("发放优惠券{}，用户:{}", couponId, user.getName());
                throw new BizException(BizCodeEnum.COUPON_NO_STOCK);
            }
        } finally {
            rLock.unlock();
            LOGGER.info("解锁{}", Thread.currentThread().getId());
        }
        return JsonData.buildSuccess();
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public JsonData initNewUserCoupon(NewUserCouponRequest newUserCouponRequest) {
        LoginUser loginUser = LoginUser.builder()
                .id(newUserCouponRequest.getUserId()).mail(null)
                .headImg(null)
                .name(newUserCouponRequest.getName())
                .build();
        List<CouponDO> couponDOList = couponMapper.selectList(new QueryWrapper<CouponDO>()
                .eq("category", CouponCategoryEnum.NEW_USER));
        LoginInterceptor.threadLocal.set(loginUser);
        for (CouponDO couponDO : couponDOList) {
            addCouponService(couponDO.getId(), CouponCategoryEnum.NEW_USER);
        }
        return JsonData.buildSuccess();
    }

    private void checkCoupon(CouponDO couponDO, long userId) {
        // 如果库存为空
        if (couponDO.getStock() <= 0) {
            throw new BizException(BizCodeEnum.COUPON_NO_STOCK);
        }

        if (couponDO.getConditionPrice().equals(CouponStatusEnum.PUBLISH.name())) {
            throw new BizException(BizCodeEnum.COUPON_CONDITION_ERROR);
        }

        // 是否在领取时间范围
        long time = CommonUtil.getCurrentTimeStamp();
        long start = couponDO.getStartTime().getTime();
        long end = couponDO.getEndTime().getTime();
        if (start > time || end < time) {
            throw new BizException(BizCodeEnum.COUPON_OUT_OF_TIME);
        }

        // 用户领取时否超限制
        int recordNum = couponRecordMapper.selectCount(new QueryWrapper<CouponRecordDO>()
                .eq("coupon_id", couponDO.getId())
                .eq("user_id", userId));
        if (recordNum >= couponDO.getUserLimit()) {
            throw new BizException(BizCodeEnum.COUPON_OUT_OF_LIMIT);
        }
    }

    private Object do2Vo(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof List) {
            List<CouponVo> result = new ArrayList<>();
            for (CouponDO couponDO : (List<CouponDO>) object) {
                CouponVo couponVo = new CouponVo();
                BeanUtils.copyProperties(couponDO, couponVo);
                result.add(couponVo);
            }
            return result;
        } else if (object instanceof CouponDO) {
            CouponVo couponVo = new CouponVo();
            BeanUtils.copyProperties(object, couponVo);
            return couponVo;
        }
        return null;
    }
}
