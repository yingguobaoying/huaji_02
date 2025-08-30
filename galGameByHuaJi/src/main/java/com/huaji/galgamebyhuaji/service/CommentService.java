package com.huaji.galgamebyhuaji.service;


import com.huaji.galgamebyhuaji.entity.Comment;
import com.huaji.galgamebyhuaji.exceptions.WriteError;
import com.huaji.galgamebyhuaji.vo.CommentWithUser;

import java.util.List;

/**
 * 此接口的操作均不会验证身份,请在调用前验证
 */
public interface CommentService {
	/**
	 * 根据资源ID来获取对应评论信息
	 * @param rId 需要获取的ID
	 * @return 打包好的评论
	 */
	List<CommentWithUser> getCommentByRId(Integer rId);

	/**
	 * 根据用户ID获取评论
	 * @param user_id 用户ID
	 * @return 获取的评论
	 */
	List<Comment> getCommentById(Integer user_id);

	/**
	 * 添加评论
	 * @param c 添加的评论
	 * @throws WriteError 数据库读写错误(小概率)
	 */
	Comment addComment(Comment c);

	/**
	 * 删除评论
	 * @param c 需要删除的评论(值评论会被当成单独的评论展示)
	 * @return 被删除的评论
	 * @throws WriteError 数据库读写错误(小概率)
	 */
	Comment deleteComment(Comment c);

	/**
	 * 更新评论
	 * @param c 更新后的评论
	 * @return 更新后的评论
	 * @throws WriteError 数据库读写错误(小概率)
	 */
	Comment updateComment(Comment c);
}