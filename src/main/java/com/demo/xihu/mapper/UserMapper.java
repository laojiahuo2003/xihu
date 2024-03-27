package com.demo.xihu.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.xihu.entity.User;
import com.demo.xihu.vo.InfoUserVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    //根据用户名查询用户
    @Select("select * from user where account=#{account}")
    User getByAccount(String account);

    //注册添加
    @Insert("insert into user(account,password,phone,username)" +
            "values(#{account},#{md5Password},#{phone},#{username}) ")
    void myInsert(String account, String md5Password,String phone,String username);


    void updateByAccount(User user);

    @Update("update user set avatar=#{avatarUrl} where id=#{id}")
    void updateAvatar(String avatarUrl,Integer id);

    @Update("update user set password=#{md5String} where id=#{id}")
    void updatePwd(String md5String, Integer id);

    @Select("select * from user where id=#{userId}")
    User getUserById(Long userId);

    InfoUserVO getUserInfo(Integer id);
}
