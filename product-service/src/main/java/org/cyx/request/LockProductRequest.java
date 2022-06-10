package org.cyx.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description LockProductRequest
 * @Author cyx
 * @Date 2021/6/27
 **/
@ApiModel(value = "锁定商品对象", description = "商品锁定对象协议")
@Data
public class LockProductRequest {
    @ApiModelProperty(value = "订单id", example = "123123")
    private String orderOutTradeNo;
    @ApiModelProperty(value = "订单项")
    private List<OrderItemRequest> orderItemList;
}
