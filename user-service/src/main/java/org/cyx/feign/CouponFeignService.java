package org.cyx.feign;

import org.cyx.request.NewUserCouponRequest;
import org.cyx.util.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Description CouponFeignService
 * @Author cyx
 * @Date 2021/4/19
 **/
@FeignClient("coupon-service")
public interface CouponFeignService {
    /**
     * 新人发放优惠券
     * */
    @PostMapping("/api/coupon/addNewUserCoupon")
    JsonData addNewUserCoupon(@RequestBody NewUserCouponRequest newUserCouponRequest);
}
