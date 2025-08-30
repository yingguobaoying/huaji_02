package com.huaji.galgamebyhuaji.entity;

import com.huaji.galgamebyhuaji.vo.CommentWithUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Comment {
    private Long commentId;

    private Integer commentUser;

    private Date commentTime;

    private Long fatherComment;

    private Integer commentRId;

    private String comment;
    private List<CommentWithUser> sumComment;

    public void addSumComment(CommentWithUser comment) {
        if (sumComment == null)
            sumComment = new ArrayList<CommentWithUser>();
        sumComment.add(comment);
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public Integer getCommentUser() {
        return commentUser;
    }

    public void setCommentUser(Integer commentUser) {
        this.commentUser = commentUser;
    }

    public Date getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(Date commentTime) {
        this.commentTime = commentTime;
    }

    public Long getFatherComment() {
        return fatherComment;
    }

    public void setFatherComment(Long fatherComment) {
        this.fatherComment = fatherComment;
    }

    public Integer getCommentRId() {
        return commentRId;
    }

    public void setCommentRId(Integer commentRId) {
        this.commentRId = commentRId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment == null ? null : comment.trim();
    }

    public List<CommentWithUser> getSumComment() {
        return sumComment;
    }

    public void setSumComment(List<CommentWithUser> sumComment) {
        this.sumComment = sumComment;
    }
}