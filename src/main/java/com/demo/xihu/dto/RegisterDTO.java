package com.demo.xihu.dto;


import com.baomidou.mybatisplus.annotation.TableField;
import jakarta.validation.constraints.Email;
import lombok.Data;

/**
 * 注册DTO
 */
@Data
public class RegisterDTO {

    private String account;
    private String password;
    private String username;
    private Integer userType;
    private String phone;
    private String sex;
    private String avatar;
    private String company;
    private String department;
    private String position;
    private String email;
    private String verifyCode;
    private Integer points;
    private String captcha;
}