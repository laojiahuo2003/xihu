package com.demo.xihu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.demo.xihu.dto.GoodregistrationDTO;
import com.demo.xihu.entity.Goodregistration;

import java.util.List;

public interface GoodregistrationService extends IService<Goodregistration> {
    void register(String token, GoodregistrationDTO goodregistrationDTO);

    void cancelRegistration(String token, Long cancelActivityId);

    List<Integer> findSubbyUserId(Integer userId);
}
