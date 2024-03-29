package com.demo.xihu.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.xihu.constant.pointName;
import com.demo.xihu.dto.RegisterDTO;
import com.demo.xihu.entity.Point;
import com.demo.xihu.entity.Userpoint;
import com.demo.xihu.exception.AccountNotFoundException;
import com.demo.xihu.mapper.PointMapper;
import com.demo.xihu.mapper.UserMapper;
import com.demo.xihu.entity.User;
import com.demo.xihu.exception.BaseException;
import com.demo.xihu.exception.PasswordErrorException;
import com.demo.xihu.mapper.UserpointMapper;
import com.demo.xihu.service.UserService;
import com.demo.xihu.utils.Md5Util;
import com.demo.xihu.utils.ThreadLocalUtil;
import com.demo.xihu.vo.InfoUserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService{
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserpointMapper userpointMapper;
    @Autowired
    private PointMapper pointMapper;

    @Override
    public InfoUserVO getById(Integer id) {
        return userMapper.getUserInfo(id);
    }

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
    public void register(RegisterDTO registerDTO) {
        //md5加密
        String md5Password = Md5Util.getMD5String(registerDTO.getPassword());
        registerDTO.setPassword(md5Password);
        User user = new User();
        BeanUtils.copyProperties(registerDTO,user);
        userMapper.insert(user);
        //增加一条积分活动记录
        UpdateWrapper<Userpoint> updateWrapper = new UpdateWrapper<>();
        Long userId = userMapper.selectOne(new QueryWrapper<User>().eq("account", registerDTO.getAccount())).getId();
        Point point = pointMapper.selectOne(new QueryWrapper<Point>().eq("name", pointName.REGISTER_USER));
        Long pointId = point.getId();
        Integer pointnum = point.getPointnum();
        userpointMapper.insert(new Userpoint().builder().userId(Math.toIntExact(userId)).pointId(pointId).updateTime(LocalDateTime.now()).build());
        addpoint(Math.toIntExact(userId),pointnum);
        //添加
        //userMapper.myInsert(account,md5Password,phone,username);
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

    @Override
    public void addpoint(Integer userId, Integer pointnum) {
        //id为userId的用户的points增加pointnum
        // 创建更新条件构造器
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        // 设置更新条件：用户ID等于指定的ID
        updateWrapper.eq("id", userId);
        // 设置更新字段：points增加指定的积分
        updateWrapper.setSql("points = points + " + pointnum);
        // 执行更新操作
        userMapper.update(updateWrapper);
    }
}
