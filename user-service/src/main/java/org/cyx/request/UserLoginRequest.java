package org.cyx.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

/**
 * @Description UserLoginRequest
 * @Author cyx
 * @Date 2021/2/19
 **/
@ApiModel(value = "登录对象", description = "用户登录请求对象")
@Data
public class UserLoginRequest {
    @ApiParam(value = "邮箱", example = "chengyxnet@163.com")
    private String mail;

    @ApiParam(value = "密码", example = "123456")
    private String pwd;

}
