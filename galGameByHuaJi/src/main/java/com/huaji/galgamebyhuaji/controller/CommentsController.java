package com.huaji.galgamebyhuaji.controller;

import com.huaji.galgamebyhuaji.constant.Constant;
import com.huaji.galgamebyhuaji.entity.Comment;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.service.CollectServlet;
import com.huaji.galgamebyhuaji.service.CommentService;
import com.huaji.galgamebyhuaji.vo.CommentWithUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentsController extends BaseController {
	final
	CommentService commentService;
	final
	CollectServlet collectServlet;
	
	@GetMapping("/Resources/getComments/{rId}")
	@ResponseBody
	public ReturnResult<CommentWithUser> getComments (@PathVariable("rId") int rId) {
		List<CommentWithUser> commentByRId = commentService.getCommentByRId(rId);
		return !commentByRId.isEmpty() ?
				ReturnResult.isTrue("获取成功", commentByRId, -1)
				: ReturnResult.isTrue("获取成功", null);
	}
	
	@PostMapping("/Resources/addComments")
	@ResponseBody
	public ReturnResult<CommentWithUser> getComments (Comment c) {
		Users loginUser = getLoginUser(true);
		c.setCommentUser(loginUser.getUserId());
		if ( MyStringUtil.isNull(c.getComment()) ) throw new OperationException("评论内容不可为空!");
		if ( c.getCommentRId() == null || c.getCommentRId() < 0 ) throw new OperationException("资源ID不可为空!");
		Comment comment = commentService.addComment(c);
		return ReturnResult.isTrue("评论成功", new CommentWithUser(comment, loginUser));
	}
	
	@GetMapping("/user/getUserCollectionList/{userId}")
	@ResponseBody
	public ReturnResult<Integer> getUserCollectionList (@PathVariable("userId") int userId) {
		Users loginUser = getLoginUser();
		if ( Constant.TOURIST.equals(loginUser) ) {
			return ReturnResult.isFalse("请先登录在进行此操作!");
		}
		return ReturnResult.isTrue("获取收藏列表成功", collectServlet.getCollectResources(loginUser.getUserId()), -1);
	}
}
