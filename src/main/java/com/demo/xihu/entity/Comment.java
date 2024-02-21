package com.demo.xihu.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 评论信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("comment")
public class Comment implements Serializable {

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;       // 评论ID
    @TableField("content")
    private String content;       // 评论内容
    @TableField("user_id")
    private Long userId;          // 评论作者ID
    @TableField("username")
    private String username;      // 评论作者姓名
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    private Date createTime;      // 创建时间
    @TableField("is_delete")
    private Integer isDelete;     // 是否删除（0：未删除；1：已删除）

    @TableField("activity_id")
    private Long activityId;      // 大会ID
    @TableField("parent_id")
    private Long parentId;    // 父评论ID（被回复的评论）
    @TableField("root_parent_id")
    private Long rootParentId;      // 根评论ID（最顶级的评论）

    private List<Comment> child;    // 本评论下的子评论


}
