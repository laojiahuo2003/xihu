package com.demo.xihu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.demo.xihu.dto.RegistrationDTO;
import com.demo.xihu.entity.Registration;

import java.util.List;

public interface RegistrationService extends IService<Registration> {
    void register(RegistrationDTO registrationDTO);

    List<Integer> findSubbyUserId(Integer userId);
}
