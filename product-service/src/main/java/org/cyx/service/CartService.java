package org.cyx.service;

import org.cyx.request.CartItemRequest;
import org.cyx.util.JsonData;
import org.cyx.vo.CartVo;

/**
 * @Description CartService
 * @Author cyx
 * @Date 2021/4/13
 **/
public interface CartService {
    JsonData addToCart(CartItemRequest cartItemRequest);

    JsonData clearCart();

    CartVo findMyCart();

    void deleteProduct(Long productId);

    void changeItemNum(CartItemRequest cartItemRequest);
}
