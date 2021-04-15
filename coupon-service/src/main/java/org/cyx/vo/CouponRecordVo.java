package org.cyx.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description CouponRecordVo
 * @Author cyx
 * @Date 2021/4/2
 **/
public class CouponRecordVo {
    /**
     * 优惠券id
     */
    @JsonProperty("coupon_id")
    private Long couponId;

    /**
     * 使用状态  可用 NEW,已使用USED,过期 EXPIRED;
     */
    @JsonProperty("user_state")
    private String useState;

    /**
     * 用户id
     */
    @JsonProperty("user_id")
    private Long userId;

    /**
     * 用户昵称
     */
    @JsonProperty("user_name")
    private String userName;

    /**
     * 优惠券标题
     */
    @JsonProperty("user_title")
    private String couponTitle;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-ddd hh:mm:ss",locale = "zh",timezone = "GMT+8")
    @JsonProperty("start_time")
    private Date startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-ddd hh:mm:ss",locale = "zh",timezone = "GMT+8")
    @JsonProperty("end_time")
    private Date endTime;

    /**
     * 订单id
     */
    @JsonProperty("order_id")
    private Long orderId;

    /**
     * 抵扣价格
     */
    private BigDecimal price;

    /**
     * 满多少才可以使用
     */
    @JsonProperty("conditionPrice")
    private BigDecimal conditionPrice;
}
