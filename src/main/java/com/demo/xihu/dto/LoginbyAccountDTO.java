package com.demo.xihu.dto;

import lombok.Data;

/**
 * 账号登陆的DTO
 */
@Data
public class LoginbyAccountDTO {
    private String account;
    private String password;
    private String captcha;

}
