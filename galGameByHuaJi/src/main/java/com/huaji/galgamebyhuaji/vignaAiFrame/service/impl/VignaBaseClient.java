package com.huaji.galgamebyhuaji.vignaAiFrame.service.impl;

import com.huaji.galgamebyhuaji.entity.AiClientConfigWithBLOBs;
import com.huaji.galgamebyhuaji.vignaAiFrame.filter.MyBaseAdvisor;
import com.huaji.galgamebyhuaji.vignaAiFrame.myenum.AiMerchantType;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.KeyServlet;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.VignaHttpClient;

import java.util.List;

public abstract class VignaBaseClient implements VignaHttpClient {
	
	protected KeyServlet keyServlet;
	protected boolean hasKey = true;
	public static VignaBaseClient getInstance (
			AiClientConfigWithBLOBs clientConfig, List<MyBaseAdvisor> filterList, boolean sort, AiMerchantType type) {
		return switch ( type ) {
			//默认为openai规范的玩意
			default -> new VignaHttpClientImpl(clientConfig, filterList, sort);
		};
	}
	
	public static VignaBaseClient getInstance (
			AiClientConfigWithBLOBs clientConfig, List<MyBaseAdvisor> filterList, AiMerchantType type) {
		return getInstance(clientConfig, filterList, false, type);
	}
	
	@Override
	public void setKeyServlet (KeyServlet keyServlet) {
		hasKey = keyServlet != null;
		this.keyServlet = keyServlet;
	}
}