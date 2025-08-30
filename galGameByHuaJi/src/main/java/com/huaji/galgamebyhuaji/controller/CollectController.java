package com.huaji.galgamebyhuaji.controller;

import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.service.CollectServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/api/user")
@ResponseBody
public class CollectController extends BaseController {
	@Autowired
	CollectServlet collectServlet;
	
	@GetMapping("/getUserCollectList")
	public ReturnResult<Integer> getUserCollectList () {
		Users loginUser = getLoginUser(true);
		List<Integer> r = collectServlet.getCollectResources(loginUser.getUserId());
		return r.isEmpty() ?
				ReturnResult.isTrue("您未收藏任何资源", null)
				: ReturnResult.isTrue("获取收藏列表成功", r, -1);
	}
}