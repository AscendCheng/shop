package org.cyx.componet;

import lombok.extern.slf4j.Slf4j;
import org.cyx.vo.PayInfoVo;
import org.springframework.stereotype.Service;

/**
 * @Description WechatStrategy
 * @Author cyx
 * @Date 2021/7/9
 **/
@Service
@Slf4j
public class WechatPayStrategy implements PayStrategy{

    @Override
    public String unifiedOrder(PayInfoVo payInfoVo) {
        return null;
    }

    @Override
    public String refund(PayInfoVo payInfoVo) {
        return null;
    }

    @Override
    public String queryPaySuccess(PayInfoVo payInfoVo) {
        return null;
    }
}
