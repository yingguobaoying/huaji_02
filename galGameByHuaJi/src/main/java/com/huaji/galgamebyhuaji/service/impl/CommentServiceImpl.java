package com.huaji.galgamebyhuaji.service.impl;


import com.huaji.galgamebyhuaji.constant.GlobalLock;
import com.huaji.galgamebyhuaji.dao.CommentMapper;
import com.huaji.galgamebyhuaji.entity.Comment;
import com.huaji.galgamebyhuaji.entity.CommentExample;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.exceptions.WriteError;
import com.huaji.galgamebyhuaji.service.CommentService;
import com.huaji.galgamebyhuaji.service.UserMxgServlet;
import com.huaji.galgamebyhuaji.vo.CommentWithUser;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author 滑稽/因果报应
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentMapper commentMapper;
    private final UserMxgServlet userMxgServlet;
    
    
    @Override
    @Cacheable(value = "commentCache", key = "#rId")
    public List<CommentWithUser> getCommentByRId(Integer rId) {
        CommentExample example = new CommentExample();
        example.createCriteria().andCommentRIdEqualTo(rId);
        List<Comment> comments = commentMapper.selectByExampleWithBLOBs(example);
        if (comments == null || comments.isEmpty()) {
            return Collections.emptyList();
        }
        List<CommentWithUser> topComments = new ArrayList<>();
        List<CommentWithUser> rootComments = new ArrayList<>();
        Map<Long, CommentWithUser> commentWithUserMap = new HashMap<>();
        final Long TOP_ID = 0L; // 顶置评论标识
        
        //构建Map并关联父子评论
        for (Comment comment : comments) {
            CommentWithUser cwu = new CommentWithUser(
                    comment,
                    userMxgServlet.getUserListMsg(comment.getCommentUser())
            );
            commentWithUserMap.put(comment.getCommentId(), cwu);
            
            Long fatherId = comment.getFatherComment();
            if (fatherId != null) {
                if (TOP_ID.equals(fatherId)) {
                    topComments.add(cwu);
                } else {
                    CommentWithUser father = commentWithUserMap.get(fatherId);
                    if (father != null) {
                        father.getOriginalComment().addSumComment(cwu); // 延迟排序
                    } else {
                        rootComments.add(cwu); // 暂时作为根评论
                    }
                }
            } else {
                rootComments.add(cwu);
            }
        }
        
        // 排序
        Comparator<CommentWithUser> timeComparator = Comparator.comparing(
                c -> c.getOriginalComment().getCommentTime() != null ?
                        c.getOriginalComment().getCommentTime() : new Date(0)
        );
        topComments.sort(timeComparator);
        rootComments.sort(timeComparator);
        commentWithUserMap.values().forEach(cwu -> {
            if (cwu.getOriginalComment().getSumComment() != null) {
                cwu.getOriginalComment().getSumComment().sort(timeComparator);
            }
        });
        
        // 合并结果
        List<CommentWithUser> result = new ArrayList<>(topComments);
        result.addAll(rootComments);
        return result;
    }
    
    
    @Override
    public List<Comment> getCommentById(Integer user_id) {
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria().andCommentUserEqualTo(user_id);
        return commentMapper.selectByExample(commentExample);
    }
    
    @Override
    @CacheEvict(value = "commentCache", key = "#c.getCommentRId()")
    public Comment addComment(Comment c) throws WriteError {
        if (c.getCommentUser() == null || c.getCommentUser().equals(1))
            throw new OperationException("错误!不存在的用户");
        GlobalLock.safeOperation(() -> {
            c.setCommentTime(new Date());
            c.setCommentId(null); // 防止覆盖已有数据导致插入失败
            WriteError.tryWrite(commentMapper.insertSelective(c));
            if (c.getCommentId() == null) throw new WriteError(1, 0);
        }, c.getCommentUser());
        return c;
    }
    
    @Override
    @CacheEvict(value = "commentCache", key = "#c.getCommentRId()")
    public Comment deleteComment(Comment c) throws WriteError {
        GlobalLock.safeOperation(() -> WriteError.tryWrite(commentMapper.deleteByPrimaryKey(c.getCommentId())), c.getCommentUser());
        return c;
    }
    
    @Override
    @CacheEvict(value = "commentCache", key = "#c.getCommentRId()")
    public Comment updateComment(Comment c) throws WriteError {
        GlobalLock.safeOperation(() -> {
            CommentExample commentExample = new CommentExample();
            commentExample.createCriteria()
                    .andCommentUserEqualTo(c.getCommentUser())
                    .andCommentIdEqualTo(c.getCommentId());
            WriteError.tryWrite(commentMapper.updateByExampleSelective(c, commentExample));
        }, c.getCommentUser());
        return c;
    }
}
