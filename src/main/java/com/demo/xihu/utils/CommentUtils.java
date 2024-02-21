package com.demo.xihu.utils;

import com.demo.xihu.entity.Comment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评论工具类
 */
public class CommentUtils {

    /**
     * 构建评论树
     * @param list
     * @return
     */
    public static List<Comment> processComments(List<Comment> list) {
        Map<Long, Comment> map = new HashMap<>();   // (id, Comment)
        List<Comment> result = new ArrayList<>();
        // 将所有根评论加入 map
        for(Comment comment : list) {
            if(comment.getParentId() == null)
                result.add(comment);
            map.put(comment.getId(), comment);
        }
        // 子评论加入到父评论的 child 中
        for(Comment comment : list) {
            Long id = comment.getParentId();
            if(id != null) {   // 当前评论为子评论
                Comment p = map.get(id);
                if(p.getChild() == null)    // child 为空，则创建
                    p.setChild(new ArrayList<>());
                p.getChild().add(comment);
            }
        }
        return result;
    }
}
