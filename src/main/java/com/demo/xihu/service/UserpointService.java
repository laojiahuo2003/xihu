package com.demo.xihu.service;


import com.demo.xihu.result.Result;
import com.demo.xihu.vo.PointsRecordVO;
import com.demo.xihu.vo.PointsStatusVO;

import java.util.List;

public interface UserpointService {
    Result addressPoint(Integer userId, Long pointId, Integer pointnum);

    List<PointsStatusVO> getPointsStatus(Integer userId);

    List<PointsRecordVO> pointsRecordList(Integer userId);
}
