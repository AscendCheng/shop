package org.cyx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Description org.cyx.OrderApplication
 * @Author cyx
 * @Date 2021/4/15
 **/
@SpringBootApplication
@MapperScan("org.cyx.mapper")
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
