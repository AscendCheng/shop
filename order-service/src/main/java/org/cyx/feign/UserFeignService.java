package org.cyx.feign;

import org.cyx.util.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Description UserFeignService
 * @Author cyx
 * @Date 2021/6/13
 **/
@FeignClient(name = "user-service")
public interface UserFeignService {
    @GetMapping("/api/address/getDetail")
    JsonData getDetail(@RequestParam("id") long id);
}
