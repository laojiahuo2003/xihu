package com.demo.xihu;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching // 基于注解使用Caffeine的缓存支持
public class XihuApplication {

    public static void main(String[] args) {
        SpringApplication.run(XihuApplication.class, args);
    }

}
