package com.huaji.galgamebyhuaji.vo;


import com.huaji.galgamebyhuaji.entity.Comment;
import com.huaji.galgamebyhuaji.entity.Users;

/**
 * @author 滑稽/因果报应
 */
public class CommentWithUser {
	
	private String userName;
	
	private String userHeadPortraitUrl;
	
	public CommentWithUser () {
	}
	
	public CommentWithUser (Comment comment, Users userListMsg) {
		this.userName = userListMsg.getUserName();
		this.userHeadPortraitUrl = userListMsg.getUserHeadPortraitUrl();
		this.originalComment = comment;
	}
	
	
	public String getUserHeadPortraitUrl () {
		return userHeadPortraitUrl;
	}
	
	public void setUserHeadPortraitUrl (String userHeadPortraitUrl) {
		this.userHeadPortraitUrl = userHeadPortraitUrl;
	}
	
	public String getUserName () {
		return userName;
	}
	
	public void setUserName (String userName) {
		this.userName = userName;
	}
	
	private Comment originalComment;
	
	public Comment getOriginalComment () {
		return originalComment;
	}
	
	public void setOriginalComment (Comment originalComment) {
		this.originalComment = originalComment;
	}
}