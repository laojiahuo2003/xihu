package com.demo.xihu.service.impl;


import com.demo.xihu.entity.Comment;
import com.demo.xihu.entity.User;
import com.demo.xihu.mapper.CommentMapper;
import com.demo.xihu.mapper.UserMapper;
import com.demo.xihu.service.CommentService;
import com.demo.xihu.utils.CommentUtils;
import com.demo.xihu.vo.CommentBlogVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    @Autowired(required = false)
    private CommentMapper commentMapper;
    @Autowired(required = false)
    private UserMapper userMapper;

    /**
     * 获取评论列表
     * @param activityId
     * @return
     */
    @Override
    public List<Comment> getCommentList(Long activityId) {
        List<Comment> list = commentMapper.getCommentList(activityId);
        return CommentUtils.processComments(list);
    }

    /**
     * 获取评论列表总数
     * @param activityId
     * @return
     */
    @Override
    public Long getCommentListTotal(Long activityId) {
        return commentMapper.getCommentListTotal(activityId);
    }

    /**
     * 获取最新评论
     * @return
     */
    @Override
    public List<CommentBlogVo> getLatestComments() {
        return commentMapper.getLatestComments();
    }

    /**
     * 添加评论
     * @param comment
     * @return
     */
    @Override
    public boolean save(Comment comment) {
        User user = userMapper.getUserById(comment.getUserId());
        comment.setUsername(user.getUsername());
        comment.setCreateTime(new Date());
        comment.setIsDelete(0);
        return commentMapper.save(comment) > 0;
    }

    /**
     * 删除评论
     * @param comment
     * @return
     */
    @Override
    public boolean removeComment(Comment comment) {
        Queue<Comment> queue = new LinkedList<>();
        queue.offer(comment);
        while(!queue.isEmpty()) {
            Comment cur = queue.poll();
            int resultNum = commentMapper.removeById(cur.getId());
            if(resultNum <= 0) return false;
            if(cur.getChild() != null) {
                List<Comment> child = cur.getChild();
                for(Comment tmp: child)
                    queue.offer(tmp);
            }
        }
        return true;
    }
}
