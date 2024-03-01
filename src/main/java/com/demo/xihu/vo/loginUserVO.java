package com.demo.xihu.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class loginUserVO implements Serializable {

    private Long id;


    private String account;


//    private String password;


    private String username;


    private Integer userType;


    private String phone;


    private String sex;


    private String avatar;


    private String company;
    //private String department;
    private String position;

    private String email;

    private String birth;

    private String token;
}
