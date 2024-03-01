package com.demo.xihu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.xihu.dto.GuestListDTO;
import com.demo.xihu.entity.Goodactivity;
import com.demo.xihu.entity.Guest;
import com.demo.xihu.mapper.ActivityMapper;
import com.demo.xihu.mapper.GoodactivityMapper;
import com.demo.xihu.mapper.GuestMapper;
import com.demo.xihu.service.GoodactivityService;
import com.demo.xihu.service.GuestService;
import com.demo.xihu.vo.GuestShowVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class GuestServiceImpl extends ServiceImpl<GuestMapper, Guest> implements GuestService {
    @Autowired
    private GuestMapper guestMapper;
    @Autowired
    private ActivityMapper activityMapper;

    @Override
    public List<GuestShowVO> getGuestsList(GuestListDTO guestListDTO) {
        String guestname = guestListDTO.getGuestname();//"李玉"
        Integer isExpert = guestListDTO.getIsExpert();//1
        String date = guestListDTO.getDate();//"5月5日"
        Integer num =guestListDTO.getNum();//2
        if (date!=null&&!date.isEmpty()){
            // 将输入的日期字符串转换为完整的日期字符串
            String fullDateStr = "2024" + "年" + date;
            // 定义日期格式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年M月d日");
            // 解析日期
            LocalDate dateTime = LocalDate.parse(fullDateStr, formatter);
            return guestMapper.selectGuestsAndActivities(dateTime,isExpert,guestname,num);
        }else{
            return guestMapper.selectGuestsAndActivities(null,isExpert,guestname,num);
        }

    }

    @Override
    public List<GuestShowVO> getGuests() {
        return guestMapper.selectGuestsAndActivities(null,null,null,null);
    }
}
