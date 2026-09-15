package com.huaji.galgamebyhuaji.controller;

import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.service.CollectServlet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/api/user")
@ResponseBody
@RequiredArgsConstructor
public class CollectController extends BaseController {
    private final CollectServlet collectServlet;
    
    
    @GetMapping("/getUserCollectList")
    public ReturnResult<Integer> getUserCollectList() {
        Users loginUser = getLoginUser();
        List<Integer> r = collectServlet.getCollectResources(loginUser.getUserId());
        return r.isEmpty() ?
                ReturnResult.isTrue("您未收藏任何资源", null)
                : ReturnResult.isTrue("获取收藏列表成功", r, -1);
    }
    
    @GetMapping("/collect/add/{id}")
    public ReturnResult<Integer> addCollect(@PathVariable("id") int id) {
        Users loginUser = getLoginUser();
        collectServlet.collectResources(loginUser.getUserId(), id);
        return ReturnResult.isTrue("收藏列表已更新", null);
    }
    
    @GetMapping("/collect/del/{id}")
    public ReturnResult<String> delCollect(@PathVariable("id") int id) {
        Users loginUser = getLoginUser();
        collectServlet.unCollectResources(loginUser.getUserId(), id);
        return ReturnResult.isTrue("收藏列表已更新", null);
    }
}
