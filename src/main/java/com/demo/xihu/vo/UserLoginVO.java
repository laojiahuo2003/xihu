package com.demo.xihu.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
//@NoArgsConstructor
//@AllArgsConstructor
@Schema(description = "登录返回的数据格式")//描述实体类DTO功能
public class UserLoginVO implements Serializable {

    @Schema(description = "主键值")//描述属性用途
    private Long id;

    @Schema(description = "用户名")
    private String account;

    @Schema(description = "jwt令牌")
    private String token;

}
