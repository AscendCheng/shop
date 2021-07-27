package org.cyx.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import lombok.extern.slf4j.Slf4j;
import org.cyx.config.AlipayConfig;
import org.cyx.enums.ProductOrderPayTypeEnum;
import org.cyx.service.ProductOrderService;
import org.cyx.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Description CallbackController
 * @Author cyx
 * @Date 2021/7/13
 **/
@RestController("/api/callback/order")
@Slf4j
public class CallbackController {
    @Autowired
    private ProductOrderService productOrderService;

    @PostMapping("/alipay")
    public String alipayCallback(HttpServletRequest request,HttpServletResponse response){
        Map<String, String> params = convertRequestParamsToMap(request);
        log.info("支付宝异步通知消息:{}",params);
        try {
            boolean signVerified = AlipaySignature.rsaCheckV1(params,AlipayConfig.ALIPAY_PUB_KEY,AlipayConfig.CHARSET,AlipayConfig.SIGN_TYPE);
            if(signVerified){
                JsonData jsonData = productOrderService.handlerOrderCallbackMsg(ProductOrderPayTypeEnum.ALIPAY,params);
                if(jsonData.isSuccess()){
                    // 通知结果确认成功，如果8次都不是success，会停止通知
                    return "success";
                }else {
                    return "failure";
                }
            }

        } catch (AlipayApiException e) {
            log.error("支付宝异步通知消息校验异常：{}",e);
        }
        return "failure";
    }

    /**
     * 将request中的参数转换成Map
     * @param request
     * @return
     */
    private static Map<String, String> convertRequestParamsToMap(HttpServletRequest request) {
        Map<String, String> paramsMap = new HashMap<>(16);
        Set<Map.Entry<String, String[]>> entrySet = request.getParameterMap().entrySet();
        for (Map.Entry<String, String[]> entry : entrySet) {
            String name = entry.getKey();
            String[] values = entry.getValue();
            int size = values.length;
            if (size == 1) {
                paramsMap.put(name, values[0]);
            } else {
                paramsMap.put(name, "");
            }
        }
        System.out.println(paramsMap);
        return paramsMap;
    }
}
