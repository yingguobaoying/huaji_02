package com.huaji.galgamebyhuaji.controller;

import com.huaji.galgamebyhuaji.dto.SelectResources;
import com.huaji.galgamebyhuaji.entity.Resources;
import com.huaji.galgamebyhuaji.entity.Tag;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.myUtil.PageUtil;
import com.huaji.galgamebyhuaji.service.SelectServlet;
import com.huaji.galgamebyhuaji.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@ResponseBody
@RequestMapping("/api/select")
@RequiredArgsConstructor
public class SelectController {
	final
	TagService tagService;
	final
	SelectServlet selectServlet;
	
	@GetMapping("/getTag")
	public ReturnResult<Tag> getTag() {
		return ReturnResult.isTrue("获取成功!", tagService.getTagMap().values().stream().toList(), -1);
	}
	
	@PostMapping("/selectResources")
	public ReturnResult<Resources> advancedSelectResources(@RequestBody SelectResources selectMsg) {
		boolean nameIsNull = MyStringUtil.isNull(selectMsg.getName());
		boolean manufacturerIsNull = MyStringUtil.isNull(selectMsg.getManufacturer());
		boolean hasTag = selectMsg.getTagList() == null || selectMsg.getTagList().isEmpty();
		boolean hasType = MyStringUtil.isNull(selectMsg.getType());
		if (!(nameIsNull || manufacturerIsNull || hasTag || hasType))
			return ReturnResult.isFalse("搜索失败,请选择至少一个条件!");
		Resources resources = new Resources();
		resources.setrName(selectMsg.getName());
		resources.setrManufacturer(selectMsg.getManufacturer());
		resources.setrType(selectMsg.getType());
		if (hasTag) {
			selectMsg.setTagSize(0);
			selectMsg.setTagList(List.of());
		}
		selectMsg.setPage(Math.max(selectMsg.getPage(), 1));
		selectMsg.setSize(Math.max(selectMsg.getSize(), 5));
		PageUtil pageUtil = new PageUtil(selectMsg.getSize(), selectMsg.getPage());
		List<Resources> list = selectServlet.searchResource(resources, selectMsg.getTagList(), selectMsg.getTagSize(), pageUtil);
		if (!list.isEmpty())
			return ReturnResult.isTrue("搜索完成!", list, null);
		else
			return ReturnResult.isFalse("搜索完成,未找到资源!缩短搜索结果或是更换搜索信息试试");
	}
	
	@PostMapping("/getSelectResourcesSize")
	public ReturnResult<Integer> getAdvancedSelectResourcesSize(@RequestBody SelectResources selectMsg) {
		boolean nameIsNull = MyStringUtil.isNull(selectMsg.getName());
		boolean manufacturerIsNull = MyStringUtil.isNull(selectMsg.getManufacturer());
		boolean hasTag = selectMsg.getTagList() == null || selectMsg.getTagList().isEmpty();
		boolean hasType = MyStringUtil.isNull(selectMsg.getType());
		if (!(nameIsNull || manufacturerIsNull || hasTag || hasType))
			return ReturnResult.isFalse("搜索失败,请选择至少一个条件!");
		Resources resources = new Resources();
		resources.setrName(selectMsg.getName());
		resources.setrManufacturer(selectMsg.getManufacturer());
		resources.setrType(selectMsg.getType());
		if (hasTag) {
			selectMsg.setSize(0);
			selectMsg.setTagList(List.of());
		}
		int searchResourceSize = selectServlet.getSearchResourceSize(resources, selectMsg.getTagList(), selectMsg.getTagSize());
		if (searchResourceSize == 0)
			return ReturnResult.isFalse("搜索完成,未找到资源!缩短搜索结果或是更换搜索词试试");
		return ReturnResult.isTrue("搜索完成!,一共%d条结果".formatted(searchResourceSize), searchResourceSize);
	}
}
