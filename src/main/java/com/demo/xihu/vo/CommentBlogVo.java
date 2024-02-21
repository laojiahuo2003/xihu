package com.demo.xihu.vo;

import lombok.Data;

@Data
public class CommentBlogVo {

    private Long activityId;        // 博客ID
    private String content;     // 评论内容
    private String username;    // 评论作者姓名
    private String title;       // 评论博客标题
}
