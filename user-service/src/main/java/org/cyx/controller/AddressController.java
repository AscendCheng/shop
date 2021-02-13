package org.cyx.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.cyx.service.AddressService;
import org.cyx.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 电商-公司收发货地址表 前端控制器
 * </p>
 *
 * @author cyx
 * @since 2021-02-08
 */
@Api(tags = "收货地址")
@RestController
@RequestMapping("/api/address")
public class AddressController {
    @Autowired
    private AddressService addressService;

    @ApiOperation("根据Id获取详情")
    @GetMapping("/getDetail")
    public JsonData getDetail(@ApiParam(value = "地址id",required = true) @RequestParam("id") String id){
        JsonData jsonData = null;
        int code = jsonData.getCode();
        return JsonData.buildSuccess(addressService.getDetail(id));
    }
}

