package org.cyx.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.cyx.enums.CouponCategoryEnum;
import org.cyx.request.NewUserCouponRequest;
import org.cyx.service.CouponService;
import org.cyx.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author cyx
 * @since 2021-03-04
 */
@Api("优惠券")
@RestController
@RequestMapping("/api/coupon")
public class CouponController {
    @Autowired
    private CouponService couponService;

    @ApiOperation("优惠券分页")
    @GetMapping("/page_coupon")
    public JsonData pageCouponList(
            @ApiParam(value = "页", required = true) @RequestParam(value = "page", defaultValue = "1") int page,
            @ApiParam(value = "页大小", required = true) @RequestParam(value = "size", defaultValue = "10") int size) {
        return couponService.pageCouponActivity(page, size);
    }

    @ApiOperation("领取优惠券")
    @GetMapping("/add/promotion/{coupon_id}")
    public JsonData addPromotionCoupon(@ApiParam(value = "优惠券Id", required = true) @PathVariable("coupon_id") long couponId) {
        return couponService.addCouponService(couponId, CouponCategoryEnum.PROMOTION);
    }

    @ApiOperation("新人优惠券")
    @PostMapping("/addNewUserCoupon")
    public JsonData addNewUserCoupon(@RequestBody NewUserCouponRequest newUserCouponRequest) {
        JsonData result = couponService.initNewUserCoupon(newUserCouponRequest);
        return result;
    }
}

