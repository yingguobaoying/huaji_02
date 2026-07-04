package com.huaji.galgamebyhuaji.service.ai;

import com.huaji.galgamebyhuaji.entity.AiClientConfigWithBLOBs;
import com.huaji.galgamebyhuaji.model.ReturnResult;

import java.util.Map;

public interface AiBastService {
	//获取配置列表
	ReturnResult<AiClientConfigWithBLOBs> getList ();
	//增加
	ReturnResult<AiClientConfigWithBLOBs> add(AiClientConfigWithBLOBs aiClientConfig,String apiKey);
	//修改
	ReturnResult<AiClientConfigWithBLOBs> update(AiClientConfigWithBLOBs aiClientConfig, String apiKey);
	//修改状态
	ReturnResult<Void> updateState(Long id,boolean newState);
	
	ReturnResult<Map<String, Integer>> getAiMerchantType ();
	
}