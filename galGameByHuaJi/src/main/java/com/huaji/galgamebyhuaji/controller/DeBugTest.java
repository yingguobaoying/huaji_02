package com.huaji.galgamebyhuaji.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DeBugTest {
	@GetMapping("/api/test")
	@ResponseBody
	public String test (HttpServletRequest request, HttpServletResponse response) {
		return "test";
	}
}