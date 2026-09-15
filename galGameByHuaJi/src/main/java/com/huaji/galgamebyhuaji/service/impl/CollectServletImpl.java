package com.huaji.galgamebyhuaji.service.impl;

import com.huaji.galgamebyhuaji.constant.GlobalLock;
import com.huaji.galgamebyhuaji.dao.FenMapper;
import com.huaji.galgamebyhuaji.entity.Fen;
import com.huaji.galgamebyhuaji.entity.FenExample;
import com.huaji.galgamebyhuaji.entity.Resources;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.exceptions.WriteError;
import com.huaji.galgamebyhuaji.service.CollectServlet;
import com.huaji.galgamebyhuaji.service.ResourcesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CollectServletImpl implements CollectServlet {
	private final FenMapper fenMapper;
	private final ResourcesService resourcesService;
	
	/**
	 * 取消收藏资源
	 *
	 * @param starter     收藏者ID
	 * @param resourcesId 被取消收藏资源的ID
	 *
	 * @return 收藏资源的信息
	 */
	@Override
	public Resources unCollectResources(Integer starter, Integer resourcesId) {
		if (starter == null || resourcesId == null) throw new OperationException("参数错误!");
		GlobalLock.safeOperation(() -> {
			FenExample example = new FenExample();
			example.createCriteria()
					.andUserIdEqualTo(starter)
					.andRIdEqualTo(resourcesId);
			int actual = fenMapper.deleteByExample(example);
			if (actual == 0) throw new OperationException("取消收藏失败!因为它不在您的收藏列表里");
			WriteError.tryWrite(actual);
		}, starter);
		return resourcesService.getResource(resourcesId);
	}
	
	/**
	 * 获取用户的收藏列表
	 *
	 * @param userId 获取的用户
	 *
	 * @return 收藏列表
	 */
	@Override
	public List<Integer> getCollectResources(Integer userId) {
		if (userId == null) throw new OperationException("参数错误!");
		FenExample example = new FenExample();
		example.createCriteria().andUserIdEqualTo(userId);
		List<Fen> fenList = fenMapper.selectByExample(example);
		if (fenList.isEmpty()) return List.of();
		List<Integer> resourcesList = new ArrayList<>();
		for (Fen fen : fenList) {
			resourcesList.add(fen.getrId());
		}
		return resourcesList;
	}
	
	/**
	 * 搜藏资源
	 *
	 * @param starter     收藏者ID
	 * @param resourcesId 被收藏资源的ID
	 *
	 * @return 收藏资源的信息
	 */
	@Override
	public Resources collectResources(Integer starter, Integer resourcesId) {
		if (starter == null || resourcesId == null) throw new OperationException("错误的资源信息!");
		GlobalLock.safeOperation(() -> {
			Fen f = new Fen();
			f.setUserId(starter);
			f.setrId(resourcesId);
			FenExample example = new FenExample();
			example.createCriteria()
					.andUserIdEqualTo(starter)
					.andRIdEqualTo(resourcesId);
			List<Fen> fens = fenMapper.selectByExample(example);
			if (fens.isEmpty())
				WriteError.tryWrite(fenMapper.insert(f));
			else {
				throw new OperationException("收藏失败,因为您已经收藏过了!");
			}
		}, starter);
		return resourcesService.getResource(resourcesId);
	}
}
