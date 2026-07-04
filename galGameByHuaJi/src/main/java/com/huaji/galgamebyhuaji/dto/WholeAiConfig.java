package com.huaji.galgamebyhuaji.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class WholeAiConfig {
	private Long id;
	/**
	 * 唯一代码(自动生成)
	 */
	private String code;
	/**
	 * 客户端名称
	 */
	private String name;
	/**
	 * 模型名称
	 */
	private String model;
	/**
	 * 基础url
	 */
	private String baseUrl;
	/**
	 * api密钥/vault存储路径
	 */
	private String apiKey;
	
	private Boolean keyIsVault;
	
	private Integer maxTokens;
	
	private Integer temperature;
	
	private Integer topP;
	
	private Integer frequencyPenalty;
	
	private Integer presencePenalty;
	
	private Boolean thinking;
	
	private String reasoningEffort;
	
	private Boolean stream;
	
	private Integer timeout;
	
	private Boolean isActive;
	
	private Date createdAt;
	
	private Date updatedAt;
	
	/**
	 * 提示词名称
	 */
	private String promptName;
	/**
	 * 描述
	 */
	private String description;
	/**
	 * 提示词内容
	 */
	
	private String content;
}