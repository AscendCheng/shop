package org.cyx.constant;

/**
 * @Description CacheKey
 * @Author cyx
 * @Date 2021/2/15
 **/
public class CacheKey {
    // 注册验证码，第一个是类型，第二个是接收号码
    public static final String CHECK_CODE_KEY = "code:%s:%s";

    // 购物车hash结果，key是唯一标识
    public static final String CART_KEY  = "cart:%s";
}
