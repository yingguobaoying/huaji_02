package com.huaji.galgamebyhuaji.enumPackage.AiEnumPackage;

import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import lombok.Getter;

@Getter
public enum AiMerchantType {
	DEEP_SEEK_CLOUD("deepSeek/迪克api", 1, "deepseek"),
	OLLAMA_CLOUD("Ollama-云模型", 2, "ollamayun"),
	OLLAMA("Ollama-本地模型", 3, "ollama"),
	OPEN_AI("使用openAi接口的模型", 4, "openai"),
	;
	private final String name;
	private final int code;
	private final String providerName;
	
	AiMerchantType(String name, int code, String providerName) {
		this.name = name;
		this.code = code;
		this.providerName = providerName;
	}
	
	public static AiMerchantType getByTypeNum (String merchant) {
		if ( OPEN_AI.getProviderName().equalsIgnoreCase(merchant) ) return OPEN_AI;
		if ( OLLAMA_CLOUD.getProviderName().equalsIgnoreCase(merchant) ) return OLLAMA_CLOUD;
		if ( OLLAMA.getProviderName().equalsIgnoreCase(merchant) ) return OLLAMA;
		if ( DEEP_SEEK_CLOUD.getProviderName().equalsIgnoreCase(merchant) ) return DEEP_SEEK_CLOUD;
		//检查是否为数字
		if ( MyStringUtil.isValidNum(merchant) ) {
			try {
				return getByTypeNum(Integer.parseInt(merchant));
			} catch ( Exception e ) {return OPEN_AI;}
		} else
			return OPEN_AI;
	}
	
	public static AiMerchantType getByTypeNum (int merchantCode) {
		if ( OPEN_AI.getCode() == merchantCode ) return DEEP_SEEK_CLOUD;
		if ( OLLAMA_CLOUD.getCode() == merchantCode ) return OLLAMA_CLOUD;
		if ( OLLAMA.getCode() == merchantCode ) return OLLAMA;
		return OPEN_AI;
	}
}
