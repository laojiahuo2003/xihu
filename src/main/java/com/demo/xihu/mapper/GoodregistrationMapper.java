package com.demo.xihu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.xihu.entity.Goodregistration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GoodregistrationMapper extends BaseMapper<Goodregistration> {
    @Select("select goodactivity_id FROM goodregistration WHERE user_id =#{userId}")
    List<Integer> selectSubbyUserId(Integer userId);
}
