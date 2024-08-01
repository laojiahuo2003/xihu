package com.demo.xihu.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("image")
public class Image {
    //主键id
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    //分类
    @TableField("image_name")
    private String imageName;
    //url
    @TableField("image_url")
    private String imageUrl;
    //类型
    @TableField("image_type")
    private String imageType;
}
