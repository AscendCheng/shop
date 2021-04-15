package org.cyx.config;

import org.cyx.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description InterceptorConfig
 * @Author cyx
 * @Date 2021/4/11
 **/
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void  addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor()).excludePathPatterns("/api/product/**");
    }
}
