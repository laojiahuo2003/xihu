package com.demo.xihu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.demo.xihu.dto.GoodregistrationDTO;
import com.demo.xihu.entity.Goodregistration;

import java.util.List;

public interface GoodregistrationService extends IService<Goodregistration> {
    void register(Integer userId, GoodregistrationDTO goodregistrationDTO);

    void cancelRegistration(Integer userId, Long cancelActivityId);

    List<Integer> findSubbyUserId(Integer userId);
}
