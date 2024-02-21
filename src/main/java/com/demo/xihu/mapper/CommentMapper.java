package com.demo.xihu.mapper;


import com.demo.xihu.entity.Comment;
import com.demo.xihu.vo.CommentBlogVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    /**
     * 根据博客ID获取评论列表
     * @param blogId
     * @return
     */
    List<Comment> getCommentList(Long blogId);

    /**
     * 获取评论列表总数
     * @param blogId
     * @return
     */
    Long getCommentListTotal(Long blogId);

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
    int save(Comment comment);

    /**
     * 根据评论ID删除评论
     * @param id
     * @return
     */
    int removeById(Long id);
}
