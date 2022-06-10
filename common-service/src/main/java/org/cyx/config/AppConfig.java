package org.cyx.config;

import feign.RequestInterceptor;
import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description AppConfig
 * @Author cyx
 * @Date 2021/3/30
 **/
@Configuration
@Data
public class AppConfig {
    @Value("${spring.redis.host}")
    private String redisHost;
    @Value("${spring.redis.port}")
    private String redisPort;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();

        //单机方式
        config.useSingleServer().setAddress("redis://" + redisHost + ":" + redisPort);
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }

    /**
     * 避免存储的key乱码，hash结构依旧乱码
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        RedisSerializer redisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(redisSerializer);
        redisTemplate.setValueSerializer(redisSerializer);
        return redisTemplate;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest httpServletRequest = attributes.getRequest();
                if (httpServletRequest == null) {
                    return;
                }
                String token = httpServletRequest.getHeader("token");
                requestTemplate.header("token", token);
            }
        };
    }
}
