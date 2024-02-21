package com.demo.xihu.controller;


import com.demo.xihu.entity.Comment;
import com.demo.xihu.result.Result;
import com.demo.xihu.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评论控制类
 */
@RestController
@RequestMapping("/dev-api/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 获取评论列表
     * @return
     */
    @GetMapping("/list")
    public Result getCommentList(Long activityId) {
        List<Comment> list = commentService.getCommentList(activityId);
        Long total = commentService.getCommentListTotal(activityId);

        Map<String, Object> map = new HashMap<>();
        map.put("commentList", list);
        map.put("total", total);
        return Result.success(map);
    }

    /**
     * 获取最新评论
     * @return
     */
    @GetMapping("/latest")
    public Result getLatestComments() {
        return Result.success(commentService.getLatestComments());
    }

    /**
     * 添加评论
     * @param comment
     * @return
     */
    @PostMapping("/add")
    public Result addComment(@RequestBody Comment comment) {
        if(commentService.save(comment))
            return Result.success("评论成功");
        return Result.error("评论失败");
    }

    /**
     * 删除评论
     * @param comm
     * @return
     */
    @PostMapping("/delete")
    public Result deleteComment(@RequestBody Comment comm) {
        if(commentService.removeComment(comm))
            return Result.success("删除评论成功");
        return Result.error("删除评论失败");
    }
}
