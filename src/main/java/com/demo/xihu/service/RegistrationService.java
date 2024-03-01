package com.demo.xihu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.demo.xihu.dto.RegistrationDTO;
import com.demo.xihu.entity.Registration;

import java.util.List;
import java.util.Map;

public interface RegistrationService extends IService<Registration> {
    void register(String token,RegistrationDTO registrationDTO);

    List<Integer> findSubbyUserId(Integer userId);

    void cancelRegistration(String token,Long cancelActivityId);
}
