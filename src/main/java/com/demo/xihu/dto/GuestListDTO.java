package com.demo.xihu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestListDTO {
    //是否为专家
    private Integer isExpert;
    //时间，null为全部，5月5日
    private String date;
    //嘉宾姓名，可模糊搜索
    private String guestname;
    //数量
    private Integer num;

}
