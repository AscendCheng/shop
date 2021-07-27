package org.cyx.feign;

import org.cyx.request.LockCouponRecordRequest;
import org.cyx.util.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @Description CouponFeignService
 * @Author cyx
 * @Date 2021/6/17
 **/
@FeignClient(name = "coupon-service")
public interface CouponFeignService {
    @GetMapping("/api/couponRecord/detail/{record_id}")
    JsonData findUserCouponRecordById(@PathVariable("record_id") long recordId);

    @PostMapping("/api/couponRecord/lockCouponRecords")
    JsonData lockCouponRecords(LockCouponRecordRequest lockCouponRecordRequest);
}
