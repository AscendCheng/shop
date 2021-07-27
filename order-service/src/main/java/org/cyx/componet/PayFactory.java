package org.cyx.componet;

import lombok.extern.slf4j.Slf4j;
import org.cyx.enums.ProductOrderPayTypeEnum;
import org.cyx.vo.PayInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description PayFactory
 * @Author cyx
 * @Date 2021/7/9
 **/
@Component
@Slf4j
public class PayFactory {
    @Autowired
    private AlipayStrategy alipayStrategy;

    @Autowired
    private WechatPayStrategy wechatPayStrategy;

    public String pay(PayInfoVo payInfoVo) {
        String type = payInfoVo.getPayType();
        if (ProductOrderPayTypeEnum.ALIPAY.name().equals(type)) {
            return alipayStrategy.unifiedOrder(payInfoVo);
        } else if (ProductOrderPayTypeEnum.WECHAT.name().equals(type)) {
            return wechatPayStrategy.unifiedOrder(payInfoVo);
        } else {
            return null;
        }
    }

    public String queryPaySuccess(PayInfoVo payInfoVo) {
        String type = payInfoVo.getPayType();
        if (ProductOrderPayTypeEnum.ALIPAY.name().equals(type)) {
            return alipayStrategy.queryPaySuccess(payInfoVo);
        } else if (ProductOrderPayTypeEnum.WECHAT.name().equals(type)) {
            return wechatPayStrategy.queryPaySuccess(payInfoVo);
        } else {
            return null;
        }
    }
}
