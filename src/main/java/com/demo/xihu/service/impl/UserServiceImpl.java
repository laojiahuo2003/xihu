package com.demo.xihu.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.xihu.exception.AccountNotFoundException;
import com.demo.xihu.mapper.UserMapper;
import com.demo.xihu.entity.User;
import com.demo.xihu.exception.BaseException;
import com.demo.xihu.exception.PasswordErrorException;
import com.demo.xihu.service.UserService;
import com.demo.xihu.utils.Md5Util;
import com.demo.xihu.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService{
    @Autowired
    private UserMapper userMapper;


    @Override
    public User login(User userLoginDTO){
        String account = userLoginDTO.getAccount();
        String password = userLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        User user = userMapper.getByAccount(account);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (user == null) {
            //账号不存在
            throw new AccountNotFoundException("账号不存在");
        }

        //密码比对
        //对前端传入的明文密码进行md5加密处理，与数据库中的密文进行比对
        password=Md5Util.getMD5String(userLoginDTO.getPassword());
        System.out.println(password+"and"+user.getPassword());
        if (!password.equals(user.getPassword())) {
            throw new PasswordErrorException("密码错误");
        }
        //3、返回实体对象
        return user;
    }


    @Override
    public User findByAccount(String account) {
        User user = userMapper.getByAccount(account);
        return user;
    }

    @Override
    public void register(String account, String password,String phone,String username) {
        //md5加密
        String md5Password = Md5Util.getMD5String(password);
        //添加
        userMapper.myInsert(account,md5Password,phone,username);
    }

    @Override
    public void updateByAccount(User userDTO) {
//        User user = findByUsername(userDTO.getUsername());
//        //设置主键
//        userDTO.setId(user.getId());
        //设置加密密码
        userDTO.setPassword(Md5Util.getMD5String(userDTO.getPassword()));
        //更新用户信息
        userMapper.updateByAccount(userDTO);
    }

    @Override
    public void myUpdateById(User userDTO) {
        //设置加密密码
        userDTO.setPassword(Md5Util.getMD5String(userDTO.getPassword()));
        //更新
        userMapper.updateById(userDTO);
    }

    @Override
    public void updateAvatar(String avatarUrl) {
        Map<String,Object> claims = ThreadLocalUtil.get();
        Integer id = (Integer) claims.get("id");
        userMapper.updateAvatar(avatarUrl,id);
    }

    @Override
    public void updatePwd(String newPwd) {
        Map<String,Object> claims = ThreadLocalUtil.get();
        Integer id = (Integer) claims.get("id");
        userMapper.updatePwd(Md5Util.getMD5String(newPwd),id);
    }
}
