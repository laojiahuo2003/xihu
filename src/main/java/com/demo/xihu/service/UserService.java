package com.demo.xihu.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.demo.xihu.entity.User;
import org.springframework.stereotype.Service;


public interface UserService extends IService<User> {

    /**
     * 登录
     * @param userLoginDTO
     * @return
     */
    User login(User userLoginDTO);

    /**
     * 根据用户名查询
     * @param account
     * @return
     */
    User findByAccount(String account);

    /**
     * 注册
     * @param account
     * @param password
     */
    void register(String account, String password,String phone,String username);


    /**
     * 根据用户名更新
     * @param user
     */
    void updateByAccount(User user);

    /**
     * 根据id更新
     * @param userDTO
     */
    void myUpdateById(User userDTO);


    /**
     * 更新头像
     * @param avatarUrl
     */
    void updateAvatar(String avatarUrl);

    void updatePwd(String newPwd);
}
