package com.huaji.galgamebyhuaji.controller;

import com.huaji.galgamebyhuaji.entity.AiClientConfigWithBLOBs;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.service.ai.AiBastService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@ResponseBody
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class AiConfigController extends BaseController {
	private final AiBastService aiBastService;
	
	@GetMapping("/getAiConfig")
	@PreAuthorize("hasRole('SYSTEM_ADMIN_JURISDICTION')")
	public ReturnResult<AiClientConfigWithBLOBs> getConfigList () {
		return aiBastService.getList();
	}
	
	/**
	 * 新建AI配置
	 */
	@PostMapping("/addAiConfig")
	@PreAuthorize("hasRole('SYSTEM_ADMIN_JURISDICTION')")
	public ReturnResult<AiClientConfigWithBLOBs> addConfig (@RequestBody AiClientConfigWithBLOBs config) {
		
		if ( config == null ) {
			throw new OperationException("配置不可为空");
		}
		
		if ( MyStringUtil.isNull(config.getApiKey()) ) {
			throw new OperationException("密钥不可为空");
		}
		
		return aiBastService.add(config, config.getApiKey());
	}
	
	/**
	 * 修改AI配置
	 */
	@PostMapping("/updateAiConfig")
	@PreAuthorize("hasRole('SYSTEM_ADMIN_JURISDICTION')")
	public ReturnResult<AiClientConfigWithBLOBs> updateConfig (
			@RequestBody AiClientConfigWithBLOBs config) {
		
		if ( config == null ) {
			throw new OperationException("配置不可为空");
		}
		
		if ( config.getId() == null ) {
			throw new OperationException("配置ID不可为空");
		}
		
		return aiBastService.update(config, config.getApiKey());
	}
	
	/**
	 * 修改启用状态
	 */
	@PostMapping("/updateAiState")
	@PreAuthorize("hasRole('SYSTEM_ADMIN_JURISDICTION')")
	public ReturnResult<Void> updateState (
			@RequestParam Long id,
			@RequestParam boolean newState) {
		if ( id == null ) {
			throw new OperationException("配置ID不可为空");
		}
		
		return aiBastService.updateState(id, newState);
	}
	
	/**
	 * 获取AI厂商类型
	 */
	@GetMapping("/getAiMerchantType")
	@PreAuthorize("hasRole('SYSTEM_ADMIN_JURISDICTION')")
	public ReturnResult<Map<String, Integer>> getMerchantType () {
		return aiBastService.getAiMerchantType();
	}
	
}
