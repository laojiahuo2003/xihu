package com.demo.xihu.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;



@Data
public class GoodregistrationDTO {

//    //用户id(用token)
//    @NotNull
//    private Long userId;

    //活动id
    @NotNull
    private Long activityId;

//    // 注册时间(服务层添加)
//    @NotNull
//    private LocalDateTime registrationTime;

    // 报名渠道
    private String registrationChannel;
}
