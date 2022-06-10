package org.cyx.componet;

import org.cyx.vo.PayInfoVo;

/**
 * @Description PayStrategy
 * @Author cyx
 * @Date 2021/7/9
 **/
public interface PayStrategy {
    /**
     * 下单
     */
    String unifiedOrder(PayInfoVo payInfoVo);

    /**
     * 退款
     */
    default String refund(PayInfoVo payInfoVo) {
        return "";
    }

    /**
     * 查询订单支付是否成功
     */
    default String queryPaySuccess(PayInfoVo payInfoVo) {
        return "";
    }


}
