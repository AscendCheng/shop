package org.cyx.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.cyx.enums.BizCodeEnum;
import org.cyx.request.AddressRequest;
import org.cyx.service.AddressService;
import org.cyx.util.JsonData;
import org.cyx.vo.AddressVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @ApiOperation("查询所有收货地址")
    @GetMapping("/list")
    public JsonData list() {
        return addressService.listAddress();
    }

    @ApiOperation("根据Id获取详情")
    @GetMapping("/getDetail")
    public JsonData getDetail(@ApiParam(value = "地址id", required = true) @RequestParam("id") String id) {
        AddressVo addressVo = addressService.getDetail(id);
        return addressVo == null ? JsonData.buildError(BizCodeEnum.ADDRESS_NO_EXITS.getMessage()) :
                JsonData.buildSuccess(addressVo);
    }

    @ApiOperation("新增收货地址")
    @PostMapping("/add")
    public JsonData add(@RequestBody AddressRequest addressRequest) {
        return addressService.add(addressRequest);
    }

    @ApiOperation("删除收货地址")
    @DeleteMapping("/del/{address_id}")
    public JsonData del(@PathVariable("address_id") String addressId) {
        return addressService.del(addressId);
    }
}

