package com.demo.xihu.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.xihu.entity.Registration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RegistrationMapper extends BaseMapper<Registration> {
    @Select("select activity_id FROM registration WHERE user_id =#{userId}")
    List<Integer> selectSubbyUserId(Integer userId);
}
