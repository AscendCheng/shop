package org.cyx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Description ProductApplication
 * @Author cyx
 * @Date 2021/4/7
 **/
@SpringBootApplication
@MapperScan("org.cyx.mapper")
@EnableTransactionManagement
public class ProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }
}
