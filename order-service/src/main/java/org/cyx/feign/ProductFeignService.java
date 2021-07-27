package org.cyx.feign;

import org.cyx.request.LockProductRequest;
import org.cyx.util.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Description ProductFeignService
 * @Author cyx
 * @Date 2021/6/15
 **/
@FeignClient(name = "product-service")
public interface ProductFeignService {
    @PostMapping("/api/cart/confirm_order_cart_item")
    JsonData confirmOrderCartItem(@RequestBody List<Long> productIds);

    @PostMapping("/api/product/lock_product")
    JsonData lockProductStock(LockProductRequest lockProductRequest);
}
