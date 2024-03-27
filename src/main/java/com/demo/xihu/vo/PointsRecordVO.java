package com.demo.xihu.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PointsRecordVO {
    private LocalDateTime time;
    private String action;
    private Integer change;
}
