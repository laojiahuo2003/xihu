package com.demo.xihu.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 精彩活动首页展示DTO
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryGoodactivitiesDTO {
    private String type;
    private Integer startIndex;
    private Integer pageSize;

}
