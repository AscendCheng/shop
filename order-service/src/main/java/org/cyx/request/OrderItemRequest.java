package org.cyx.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description OrderItemRequest
 * @Author cyx
 * @Date 2021/6/27
 **/
@ApiModel(value = "商品锁定对象",description = "商品锁定对象协议")
@Data
public class OrderItemRequest {
    @ApiModelProperty(value = "商品Id",example = "123123")
    @JsonProperty("product_id")
    private long productId;

    @ApiModelProperty(value = "购买数量")
    @JsonProperty("buy_num")
    private int buyNum;
}
