package org.cyx.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description NewUserCouponRequest
 * @Author cyx
 * @Date 2021/4/5
 **/
@Data
public class NewUserCouponRequest {
    @ApiModelProperty(value = "用户id",example = "id")
    private long userId;

    @ApiModelProperty(value = "名称",example = "name")
    private String name;
}
