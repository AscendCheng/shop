package org.cyx.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @Description RepayOrderRequest
 * @Author cyx
 * @Date 2021/7/23
 **/
@Data
public class RepayOrderRequest {
    @JsonProperty("out_trade_no")
    private String outTradeNo;

    @JsonProperty("pay_type")
    private String payType;

    @JsonProperty("client_type")
    private String clientType;
}
