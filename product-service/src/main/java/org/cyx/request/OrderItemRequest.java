package org.cyx.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @Description OrderItemRequest
 * @Author cyx
 * @Date 2021/6/27
 **/
@Data
public class OrderItemRequest {
    @JsonProperty("product_id")
    private long productId;

    @JsonProperty("buy_num")
    private int buyNum;
}
