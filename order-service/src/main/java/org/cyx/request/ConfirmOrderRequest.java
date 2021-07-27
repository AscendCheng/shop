package org.cyx.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description ConfirmOrderRequest
 * @Author cyx
 * @Date 2021/4/17
 **/
@Data
public class ConfirmOrderRequest {
    @JsonProperty("coupon_record_id")
    private Long couponRecordId;

    @JsonProperty("product_ids")
    private List<Long> productIds;

    @JsonProperty("pay_type")
    private String payType;

    @JsonProperty("client_type")
    private String clientType;

    @JsonProperty("address_id")
    private Long addressId;

    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    @JsonProperty("real_pay_amount")
    private BigDecimal realPayAmount;

    private String token;
}
