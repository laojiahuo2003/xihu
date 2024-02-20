package com.demo.xihu.controller;


import com.demo.xihu.dto.RegistrationDTO;
import com.demo.xihu.result.Result;
import com.demo.xihu.service.RegistrationService;
import com.demo.xihu.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

//import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/registration")
@Slf4j
@Tag(name = "用户_活动相关接口", description = "报名")
public class RegistrationController {
    @Autowired
    private RegistrationService registrationService;

    // TODO：这里报名需要修改活动表的订阅总数
    @PostMapping
    @Operation(summary = "活动报名")
    public Result registerForActivity(@RequestBody @Validated RegistrationDTO registrationDTO) {
        //参数非空已检验
        registrationService.register(registrationDTO);
        return Result.success("报名成功");
    }

    @DeleteMapping
    @Operation(summary = "取消报名")
    public Result cancelRegistration(@RequestBody @NotNull Long cancelActivityId) {
        //registrationService.cancelRegistration(cancelActivityId);
        return Result.success("取消成功");
    }




}
