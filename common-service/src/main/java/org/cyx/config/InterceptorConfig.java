package org.cyx.config;

import lombok.extern.slf4j.Slf4j;
import org.cyx.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description InterceptorConfig
 * @Author cyx
 * @Date 2021/2/23
 **/
@Configuration
@Slf4j
public class InterceptorConfig implements WebMvcConfigurer {
    private LoginInterceptor loginInterceptor(){
        return new LoginInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor())
                .addPathPatterns("/api/*/**")
                .excludePathPatterns("/api/user/register","/api/user/login","/api/notify/**","/api/coupon/addNewUserCoupon");
    }
}
