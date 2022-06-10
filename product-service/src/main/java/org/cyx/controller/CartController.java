package org.cyx.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.cyx.request.CartItemRequest;
import org.cyx.service.CartService;
import org.cyx.util.JsonData;
import org.cyx.vo.CartItemVo;
import org.cyx.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description CartController
 * @Author cyx
 * @Date 2021/4/13
 **/
@RestController
@Api("购物车")
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @ApiOperation("添加到购物车")
    @PostMapping("/add")
    public JsonData addCart(@ApiParam("购物项") @RequestBody CartItemRequest cartItemRequest) {
        return cartService.addToCart(cartItemRequest);
    }

    @ApiOperation("清空购物车")
    @GetMapping("/clear")
    public JsonData clear() {
        return cartService.clearCart();
    }

    @ApiOperation("查看我的购物车")
    @GetMapping("/myCart")
    public JsonData findMyCart() {
        CartVo cartVo = cartService.findMyCart();
        return JsonData.buildSuccess(cartVo);
    }

    @ApiOperation("删除购物项")
    @DeleteMapping("/delete/{product_id}")
    public JsonData deleteProduct(@ApiParam("商品id") @PathVariable("product_id") Long productId) {
        cartService.deleteProduct(productId);
        return JsonData.buildSuccess();
    }

    @ApiOperation("修改商品数量")
    @PostMapping("/changeItemNum")
    public JsonData changeItemNum(@ApiParam("购物项") @RequestBody CartItemRequest cartItemRequest) {
        cartService.changeItemNum(cartItemRequest);
        return JsonData.buildSuccess();
    }

    @ApiOperation("下单商品最新信息")
    @PostMapping("/confirm_order_cart_item")
    public JsonData confirmOrderCartItem(@RequestBody List<Long> productIds) {
        List<CartItemVo> cartItemVoList = cartService.confirmOrderCartItems(productIds);
        return JsonData.buildSuccess(cartItemVoList);
    }
}
