package com.demo.xihu.utils;

import com.demo.xihu.dto.DateActivitiesVO;
import com.demo.xihu.vo.ActivityListVO;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ActivitySorter {

    public static List<DateActivitiesVO> sortActivitiesByDate(List<ActivityListVO> activities) {
        // 对整个活动列表进行排序
        activities.sort(Comparator.comparing(ActivityListVO::getStartTime));

        // 使用 TreeMap 保证日期的排序，并将其转换为 DateActivities 列表
        return new TreeMap<>(
                activities.stream()
                        .collect(Collectors.groupingBy(
                                activity -> activity.getStartTime().toLocalDate(),
                                Collectors.toList()
                        ))
        ).entrySet().stream()
                .map(entry -> new DateActivitiesVO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}