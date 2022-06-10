package org.cyx.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import java.util.Date;

/**
 * @Description AddressRequest
 * @Author cyx
 * @Date 2021/2/24
 **/
@Data
@ApiModel(value = "地址对象", description = "新增收货地址对象")
public class AddressRequest {
    /**
     * 是否默认收货地址：0->否；1->是
     */
    @ApiParam(value = "是否为默认收货地址", example = "0")
    private Integer defaultStatus;

    /**
     * 收发货人姓名
     */
    @ApiParam(value = "收货人姓名", example = "cyx")
    private String receiveName;

    /**
     * 收货人电话
     */
    @ApiParam(value = "收货人电话", example = "13123456789")
    private String phone;

    /**
     * 省/直辖市
     */
    @ApiParam(value = "省", example = "江苏")
    private String province;

    /**
     * 市
     */
    @ApiParam(value = "市", example = "苏州")
    private String city;

    /**
     * 区
     */
    @ApiParam(value = "区", example = "工业园区")
    private String region;

    /**
     * 详细地址
     */
    @ApiParam(value = "详细地址", example = "创意产业园")
    private String detailAddress;

    private Date createTime;

}
