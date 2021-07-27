package org.cyx.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description LockCouponRecordRequest
 * @Author cyx
 * @Date 2021/6/4
 **/
@Data
public class LockCouponRecordRequest {
    /**
     * 锁定的优惠券Id
     * */
    @ApiModelProperty(value = "锁定的优惠券Id",example = "[1,2,3]")
    private List<Long> lockCouponRecordIds;

    /**
     * 绑定的订单号
     * */
    @ApiModelProperty(value = "绑定的订单号",example = "123")
    private String orderOutTradeNo;
}
