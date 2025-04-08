package com.wm.ai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({"com.wm.ai.mapper"})
public class WmSpringAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(WmSpringAiApplication.class, args);
    }
}
