package com.demo.xihu.dto;


import com.baomidou.mybatisplus.annotation.TableField;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDTO {

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
    @NotNull
    private String registrationChannel;
}
