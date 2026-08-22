package com.huaji.galgamebyhuaji.controller;

import com.huaji.galgamebyhuaji.vignaAiFrame.service.VignaAiChat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Slf4j
public class test {
	private final VignaAiChat chat;
	public void testChat(){
	
	}
}