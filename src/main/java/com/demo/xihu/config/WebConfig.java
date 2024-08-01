package com.demo.xihu.config;

import com.demo.xihu.interceptors.AdminAccessInterceptor;
import com.demo.xihu.interceptors.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
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
        registry.addInterceptor(loginInterceptor).excludePathPatterns("/dev-api/user/login","/dev-api/user/register","/swagger-ui/**", "/v3/api-docs/**","/dev-api/activities/list/**","/dev-api/captcha/**","/dev-api/sms/**","/dev-api/comment/**","/dev-api/user/token","/dev-api/upload","/image/**")
                .excludePathPatterns("/dev-api/goodactivities/list/**")
                .excludePathPatterns("/dev-api/guests/**")
                .excludePathPatterns("/dev-api/poster/**")
                .excludePathPatterns("/dev-api/images/**");
        // 添加管理员权限拦截器，拦截所有admin开头的路径
        registry.addInterceptor(adminAccessInterceptor).addPathPatterns("/dev-api/admin/**");
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 添加图片资源处理
        registry.addResourceHandler("/image/**")//指定了资源的访问路径
                .addResourceLocations("file:/image/");//表示图片资源存放在服务器的文件系统中的 "/image/" 目录下。
    }
}
