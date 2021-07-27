package org.cyx.controller;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.cyx.enums.BizCodeEnum;
import org.cyx.request.LockCouponRecordRequest;
import org.cyx.service.CouponRecordService;
import org.cyx.util.JsonData;
import org.cyx.vo.CouponRecordVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author cyx
 * @since 2021-03-04
 */
@RestController
@RequestMapping("/api/couponRecord")
public class CouponRecordController {
    @Autowired
    private CouponRecordService couponRecordService;

    @GetMapping("/page")
    @ApiOperation(value = "分页查询个人优惠券")
    public JsonData page(@RequestParam(value = "page", defaultValue = "1") int page,
                         @RequestParam(value = "size", defaultValue = "10") int size) {
        Map<String, Object> result = couponRecordService.page(size, page);
        return JsonData.buildSuccess(result);
    }

    @ApiOperation("查询优惠券详情记录")
    @GetMapping("/detail/{record_id}")
    public JsonData getCouponRecordDetail(@ApiParam(value = "记录Id") @PathVariable("record_id") long recordId) {
        CouponRecordVo couponRecordVo = couponRecordService.findById(recordId);
        return couponRecordVo == null ? JsonData.buildError(BizCodeEnum.COUPON_NO_EXITS.getMessage())
                : JsonData.buildSuccess(couponRecordVo);
    }

    @ApiOperation("rpc锁定优惠券")
    @PostMapping("/lockCouponRecords")
    public JsonData lockCouponRecords(@ApiParam(value = "锁定优惠券对象")@RequestBody LockCouponRecordRequest lockCouponRecordRequest){
        return couponRecordService.lockCouponRecords(lockCouponRecordRequest);
    }
}

