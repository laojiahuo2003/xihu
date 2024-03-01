//package com.demo.xihu.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
//
//@Configuration
//public class UploadConfiguration extends WebMvcConfigurationSupport {
//    @Override
//    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/image/**")//这个将应用到url中
//                .addResourceLocations("file:/image/");//这里填的是图片的绝对父路径
//
//
//
//    }
//}
/*
当你扩展 WebMvcConfigurationSupport 类（如你在 UploadConfiguration 类中所做的），这会禁用Spring Boot的自动MVC配置，包括拦截器、资源处理器、消息转换器等的自动注册。这可能是为什么第一个配置（UploadConfiguration）会影响第二个配置（WebConfig）的原因。

WebMvcConfigurer 接口是用来自定义Spring Boot的自动配置，而不是完全替代它。当你实现 WebMvcConfigurer 时，你可以添加额外的配置，但Spring Boot的其他自动配置仍然有效。但是，当你扩展 WebMvcConfigurationSupport 时，Spring Boot的自动配置会被完全禁用，这意味着你需要手动配置所有东西，包括拦截器、视图解析器、资源处理器等。

为了解决你的问题，你可以采取以下步骤：

移除 WebMvcConfigurationSupport 扩展：不要扩展 WebMvcConfigurationSupport，因为它会禁用Spring Boot的自动配置。相反，实现 WebMvcConfigurer 接口以保留Spring Boot的自动配置。

合并配置：将 UploadConfiguration 和 WebConfig 合并到一个配置类中，并实现 WebMvcConfigurer 接口。这样，你可以同时添加资源处理器和拦截器的配置。

确保Swagger UI配置：确保Swagger UI资源的正确映射，如果需要的话。

这里是一个合并后的配置示例：
 */