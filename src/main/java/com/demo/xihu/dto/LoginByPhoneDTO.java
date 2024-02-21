package com.demo.xihu.dto;

import lombok.Data;

/**
 * 手机号登陆
 */
@Data
public class LoginByPhoneDTO {
    private String phone;
    private String verifyCode;
    private String captcha;

}