package org.cyx.config;

import org.cyx.util.OssBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @Description OssConfig
 * @Author cyx
 * @Date 2021/2/16
 **/
@Configuration
public class OssConfig {
    @Value("${oss.secretId}")
    private String secretId;

    @Value("${oss.secretKey}")
    private String secretKey;

    @Value("${oss.region}")
    private String region;

    @Autowired
    private OssBuilder ossBuilder;
}
