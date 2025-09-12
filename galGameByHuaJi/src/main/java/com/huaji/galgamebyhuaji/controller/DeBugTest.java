package com.huaji.galgamebyhuaji.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DeBugTest {
	@GetMapping("/api/user/test/root")
	@ResponseBody
	@PreAuthorize("hasRole('ROOT_JURISDICTION')")
	public String testROOT_JURISDICTION(HttpServletRequest request, HttpServletResponse response) {
		return "test";
	}
	
	@GetMapping("/api/user/test/admin")
	@ResponseBody
	@PreAuthorize("hasRole('ADMIN_JURISDICTION')")
	public String testADMIN_JURISDICTION(HttpServletRequest request, HttpServletResponse response) {
		return "test";
	}
	
	@GetMapping("/api/user/test/rAdmin")
	@ResponseBody
	@PreAuthorize("hasRole('RESOURCES_ADMIN_JURISDICTION')")
	public String testRESOURCES_ADMIN_JURISDICTION(HttpServletRequest request, HttpServletResponse response) {
		return "test";
	}
	
	@GetMapping("/api/user/test/user")
	@ResponseBody
	@PreAuthorize("hasRole('USERS_JURISDICTION')")
	public String testUSERS_JURISDICTION(HttpServletRequest request, HttpServletResponse response) {
		return "test";
	}
	
	@GetMapping("/api/user/test/else")
	@ResponseBody
	@PreAuthorize("hasRole('NOT_VALIDATED')")
	public String testNOT_VALIDATED(HttpServletRequest request, HttpServletResponse response) {
		return "test";
	}
	@GetMapping("/api/test")
	@ResponseBody
	public String test(HttpServletRequest request, HttpServletResponse response) {
		return "test";
	}
}
