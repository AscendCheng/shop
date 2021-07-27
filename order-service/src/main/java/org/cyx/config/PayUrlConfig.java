package org.cyx.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @Description PayUrlConfig
 * @Author cyx
 * @Date 2021/7/7
 **/
@Configuration
@Data
public class PayUrlConfig {
    @Value("${alipay.success_return_url}")
    private String alipaySuccessReturnUrl;

    @Value("${alipay.callback_url}")
    private String alipayCallbackUrl;

}
