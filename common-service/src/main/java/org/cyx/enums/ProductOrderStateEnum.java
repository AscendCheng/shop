package org.cyx.enums;

/**
 * @Description ProductOrderStateEnum
 * @Author cyx
 * @Date 2021/4/15
 **/
public enum ProductOrderStateEnum {
    /**
     * 未支付订单
     */
    NEW,
    /**
     * 已经支付订单
     */
    PAY,
    /**
     * 超时取消订单
     */
    CANCEL;
}
