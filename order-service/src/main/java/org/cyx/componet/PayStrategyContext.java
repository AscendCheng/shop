package org.cyx.componet;

import org.cyx.vo.PayInfoVo;

/**
 * @Description PayStrategyContext
 * @Author cyx
 * @Date 2021/7/9
 **/
public class PayStrategyContext {
    private PayStrategy payStrategy;

    public PayStrategyContext(PayStrategy payStrategy) {
        this.payStrategy = payStrategy;
    }

    public String executeUnifiedOrder(PayInfoVo payInfoVo){
        return this.payStrategy.unifiedOrder(payInfoVo);
    }

    public String executeQueryPaySuccess(PayInfoVo payInfoVo){
        return this.payStrategy.queryPaySuccess(payInfoVo);
    }
}
