package com.huaji.galgamebyhuaji.vignaAiFrame.service;//package com.huaji.galgamebyhuaji.service.ai;

import com.huaji.galgamebyhuaji.entity.AiClassification;

public interface AiClassificationServlet {
	String user_view_cache_key = "Vigna_thinks";
	
	/**
	 * 判断用户是否可以使用这个配置
	 */
	boolean userCanSee (int userId, long configId);
	
	void addDate (AiClassification classification);
	
	void delDate (Long classificationId);
}