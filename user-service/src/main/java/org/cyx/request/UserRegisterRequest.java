package org.cyx.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

/**
 * @Description UserRegisterRequest
 * @Author cyx
 * @Date 2021/2/18
 **/
@ApiModel(value = "用户注册对象", description = "用户注册请求对象")
@Data
public class UserRegisterRequest {
    @ApiParam(value = "昵称", example = "chengyx")
    private String name;

    @ApiParam(value = "密码", example = "123456")
    private String pwd;

    @ApiParam(value = "头像", example = "https://shop-user-service-1258865434.cos.ap-beijing.myqcloud.com/user-head/2021/02/18/ceshi.jpg")
    @JsonProperty("head_img")
    private String headImg;

    @ApiParam(value = "签名", example = "个签")
    private String slogan;

    @ApiParam(value = "0表示女，1表示男", example = "0")
    private String sex;

    @ApiParam(value = "邮箱", example = "chengyxnet@163.com")
    private String mail;

    @ApiParam(value = "验证码", example = "123456")
    private String code;
}
