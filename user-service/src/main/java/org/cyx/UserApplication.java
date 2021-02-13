package org.cyx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Description UserApplication
 * @Author cyx
 * @Date 2021/2/8
 **/
@SpringBootApplication
@MapperScan("org.cyx.mapper")
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
