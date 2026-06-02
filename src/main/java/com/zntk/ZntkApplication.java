package com.zntk;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.zntk.mapper")
@SpringBootApplication
public class ZntkApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZntkApplication.class, args);
    }
}