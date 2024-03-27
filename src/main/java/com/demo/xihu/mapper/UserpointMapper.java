package com.demo.xihu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.xihu.entity.Userpoint;
import com.demo.xihu.result.Result;
import com.demo.xihu.vo.PointsRecordVO;
import com.demo.xihu.vo.PointsStatusVO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserpointMapper extends BaseMapper<Userpoint> {


    List<PointsStatusVO> PointsStatusList(Integer userId, LocalDateTime newDate);

    List<PointsRecordVO> userpointList(Integer userId);
}
