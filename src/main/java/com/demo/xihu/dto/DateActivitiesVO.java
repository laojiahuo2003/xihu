package com.demo.xihu.dto;

import com.demo.xihu.vo.ActivityListVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateActivitiesVO {
    private LocalDate date;
    private List<ActivityListVO> activities;
}