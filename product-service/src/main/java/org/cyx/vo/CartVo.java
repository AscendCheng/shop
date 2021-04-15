package org.cyx.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description CartVo
 * @Author cyx
 * @Date 2021/4/13
 **/
public class CartVo {
    /**
     * 购物车商品列表
     */
    @JsonProperty("cart_items")
    private List<CartItemVo> cartItems;

    /**
     * 购物车总件数
     */
    @JsonProperty("total_num")
    private Integer totalNum;

    /**
     * 购物车总价格
     */
    @JsonProperty("total_price")
    private BigDecimal totalPrice;

    /**
     * 购物车实际支付价格
     */
    @JsonProperty("real_pay_price")
    private BigDecimal realPayPrice;

    public List<CartItemVo> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItemVo> cartItems) {
        this.cartItems = cartItems;
    }

    public Integer getTotalNum() {
        if (cartItems != null) {
            return cartItems.stream().mapToInt(CartItemVo::getBuyNum).sum();
        }
        return 0;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }

    public BigDecimal getTotalPrice() {
        BigDecimal amount = BigDecimal.ZERO;
        if (cartItems != null) {
            for (CartItemVo cartItem : cartItems) {
                amount = amount.add(cartItem.getTotalAmount());
            }
        }
        return amount;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getRealPayPrice() {
        return realPayPrice;
    }

    public void setRealPayPrice(BigDecimal realPayPrice) {
        this.realPayPrice = realPayPrice;
    }
}
