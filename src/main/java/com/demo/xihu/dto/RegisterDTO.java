package com.demo.xihu.dto;


import lombok.Data;

/**
 * 注册DTO
 */
@Data
public class RegisterDTO {
    private String account;
    private String phone;
    private String username;
    private String password;
    private String verifyCode;
    private String captcha;
}