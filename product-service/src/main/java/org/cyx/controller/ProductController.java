package org.cyx.controller;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.cyx.request.LockProductRequest;
import org.cyx.service.ProductService;
import org.cyx.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author cyx
 * @since 2021-04-07
 */
@RestController
@RequestMapping("/api/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @ApiOperation("商品分页列表")
    @GetMapping("/page")
    public JsonData pageProductList(
            @ApiParam(value = "页", required = true) @RequestParam(value = "page", defaultValue = "1") int page,
            @ApiParam(value = "页大小", required = true) @RequestParam(value = "size", defaultValue = "10") int size) {
        Map<String, Object> pageResult = productService.page(page, size);
        return JsonData.buildSuccess(pageResult);
    }

    @ApiOperation("商品详情")
    @GetMapping("/detail/{product_id}")
    public JsonData detail(@ApiParam(value = "商品ID", required = true) @PathVariable("product_id") String productId) {
        return JsonData.buildSuccess(productService.findDetailById(productId));
    }

    @PostMapping("/lock_product")
    public JsonData lockProduct(@ApiParam("商品库存锁定") @RequestBody LockProductRequest lockProductRequest) {
        JsonData result = productService.lockProduct(lockProductRequest);
        return JsonData.buildSuccess(result);
    }
}

