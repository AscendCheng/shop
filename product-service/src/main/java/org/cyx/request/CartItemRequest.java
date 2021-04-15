package org.cyx.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description CartItemRequest
 * @Author cyx
 * @Date 2021/4/13
 **/
@Data
public class CartItemRequest {
    @ApiModelProperty(value = "商品ID",example = "1")
    @JsonProperty("product_id")
    private Long productId;

    @ApiModelProperty(value = "购买数量",example = "0")
    @JsonProperty("buy_num")
    private Integer buyNum;

}
