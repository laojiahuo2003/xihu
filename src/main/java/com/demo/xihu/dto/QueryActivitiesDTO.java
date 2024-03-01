package com.demo.xihu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
{
    "date":"5月7日",
    "type":"平行论坛",
    "num":"2"
}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryActivitiesDTO {
    private String date;
    private String type;
    private Integer num;
}
