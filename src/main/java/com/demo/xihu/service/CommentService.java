package com.demo.xihu.service;

import com.demo.xihu.entity.Comment;
import com.demo.xihu.vo.CommentBlogVo;

import java.util.List;

public interface CommentService {

    /**
     * 获取评论列表
     * @param activityId
     * @return
     */
    List<Comment> getCommentList(Long activityId);

    /**
     * 获取评论列表总数
     * @param activityId
     * @return
     */
    Long getCommentListTotal(Long activityId);

    /**
     * 获取最新评论
     * @return
     */
    List<CommentBlogVo> getLatestComments();

    /**
     * 添加评论
     * @param comment
     * @return
     */
    boolean save(Comment comment);

    /**
     * 删除评论
     * @param comment
     * @return
     */
    boolean removeComment(Comment comment);
}
