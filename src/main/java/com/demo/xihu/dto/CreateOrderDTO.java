package com.demo.xihu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderDTO implements Serializable {
    //商品id
    private Long goodId;
    //用户id
    private Long userId;
    //收件人姓名
    private String receiverName;
    //收件人手机
    private String receiverPhone;
    //收件地址
    private String receiverAddress;


}
