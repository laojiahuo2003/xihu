package com.demo.xihu.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InfoUserVO {

    private Long id;

    private String account;

//    private String password;

    private String username;

    private Integer userType;

    private String phone;

    private String sex;

    private String avatar;

    private String company;

    private String position;

    private String email;

    private String birth;

    private Integer points;
}
