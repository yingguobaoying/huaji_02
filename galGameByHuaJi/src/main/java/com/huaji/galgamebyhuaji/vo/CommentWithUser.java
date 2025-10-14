package com.huaji.galgamebyhuaji.vo;


import com.huaji.galgamebyhuaji.entity.Comment;
import com.huaji.galgamebyhuaji.entity.Users;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 滑稽/因果报应
 */
@Getter
@Setter
public class CommentWithUser {
	
	private String userName;
	
	private String userHeadPortraitUrl;
	
	public CommentWithUser() {
	}
	
	public CommentWithUser(Comment comment, Users userListMsg) {
		this.userName = userListMsg.getUserName();
		this.userHeadPortraitUrl = userListMsg.getUserHeadPortraitUrl();
		this.originalComment = comment;
	}
	
	private Comment originalComment;
	
}
