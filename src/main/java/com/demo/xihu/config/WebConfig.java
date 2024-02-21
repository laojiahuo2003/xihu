package com.demo.xihu.config;

import com.demo.xihu.interceptors.AdminAccessInterceptor;
import com.demo.xihu.interceptors.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;
    @Autowired
    private AdminAccessInterceptor adminAccessInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //添加登录拦截器
         registry.addInterceptor(loginInterceptor).excludePathPatterns("/dev-api/user/login","/dev-api/user/register","/swagger-ui/**", "/v3/api-docs/**","/dev-api/activities/list/**","/dev-api/captcha/**","/dev-api/sms/**","/dev-api/comment/**");
        // 添加管理员权限拦截器，拦截所有admin开头的路径
        registry.addInterceptor(adminAccessInterceptor).addPathPatterns("/dev-api/admin/**");
    }
}
