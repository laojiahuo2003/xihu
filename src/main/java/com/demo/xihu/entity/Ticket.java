package com.demo.xihu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("ticket")
public class Ticket {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;


    @TableField("title")
    private String title;


    @TableField("stock")
    private Integer stock;
}
