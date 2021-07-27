package org.cyx.feign;

import org.cyx.util.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Description ProductOrderFeignService
 * @Author cyx
 * @Date 2021/6/8
 **/
@FeignClient(name = "order-service")
public interface ProductOrderFeignService {
    @GetMapping("/api/order/v1/queryState")
    JsonData queryProductOrderState(@RequestParam("out_trade_no") String outTradeNo);
}
