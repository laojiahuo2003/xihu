package com.demo.xihu.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//import javax.validation.constraints.Email;
//import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    @NotNull
    private Long id;

    //账号
    @TableField("account")
    private String account;

    //密码
    //@JsonIgnore转换为json时自动忽略
    @TableField("password")
    private String password;

    // 姓名
    @TableField("username")
    private String username;

    //用户类别 0管理员，1普通用户
    @TableField("userType")
    private Integer userType;

    // 手机号
    @TableField("phone")
    private String phone;

    // 性别 0 男 1 女
    @TableField("sex")
    private Integer sex;

    // 头像
    @TableField("avatar")
    private String avatar;

    // 公司
    @TableField("company")
    private String company;

    // 部门
    @TableField("department")
    private String department;

    // 职位
    @TableField("position")
    private String position;

    // 邮箱地址
    @TableField("email")
    //参数校验
    @Email
    private String email;

}
