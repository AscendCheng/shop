package org.cyx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Description UserApplication
 * @Author cyx
 * @Date 2021/2/8
 **/
@SpringBootApplication
@MapperScan("org.cyx.mapper")
@EnableTransactionManagement
public class CouponApplication {
    public static void main(String[] args) {
        SpringApplication.run(CouponApplication.class, args);
    }
}
