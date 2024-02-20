package com.demo.xihu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    /**
     *  当前跨域请求最大有效时长。这里默认1天
     */
    private static final long MAX_AGE = 24 * 60 * 60;

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 1 设置访问源地址
        corsConfiguration.addAllowedOrigin("*");
        // 2 设置访问源请求头
        corsConfiguration.addAllowedHeader("*");
        // 3 设置访问源请求方法
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.setMaxAge(MAX_AGE);
        // 4 对接口配置跨域设置
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }
}
//
//@Configuration
//public class GlobalCorsConfig {
//    @Bean
//    public CorsFilter corsFilter() {
////1.添加 CORS配置信息
//        CorsConfiguration config = new orsonfiguration();
////放行哪些原始域
//        config.addAllowed0riginPattern("*");//2.4.0后的写法
//// config.addAllowedorigin("*");
////是否发送 Cookie
//        config.setAllowCredentials(true);
////放行哪些请求方式
//        config.addAllowedMethod("*");
////放行哪些原始请求头部信息
//        config.addAllowedHeader("*");
////暴露哪些头部信息
//        config.addExposedHeader("*");
////2.添加映射路径
//        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
//        corsConfigurationSource.registerCorsConfiguration(pattern:"/**", config);
//        //3.返回新的CorsFilterreturn new CorsFilter(corsConfigurationSource);
//    }
//}