package org.cyx.service;

import org.cyx.enums.CouponCategoryEnum;
import org.cyx.model.CouponDO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.cyx.request.NewUserCouponRequest;
import org.cyx.util.JsonData;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author cyx
 * @since 2021-03-04
 */
public interface CouponService extends IService<CouponDO> {
    JsonData pageCouponActivity(int page, int size);

    JsonData addCouponService(long couponId, CouponCategoryEnum category);

    JsonData initNewUserCoupon(NewUserCouponRequest newUserCouponRequest);
}
