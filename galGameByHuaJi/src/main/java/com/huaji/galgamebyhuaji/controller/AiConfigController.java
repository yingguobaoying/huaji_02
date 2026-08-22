package com.huaji.galgamebyhuaji.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class AiConfigController extends BaseController {
}