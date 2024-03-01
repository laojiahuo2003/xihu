package com.demo.xihu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.xihu.entity.Guest;
import com.demo.xihu.vo.GuestShowVO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface GuestMapper extends BaseMapper<Guest> {

    List<GuestShowVO> selectGuestsAndActivities(LocalDate dateTime, Integer isExpert, String guestname,Integer num);
}
