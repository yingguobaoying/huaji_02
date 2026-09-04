package com.huaji.galgamebyhuaji.vignaAiFrame.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.huaji.galgamebyhuaji.vignaAiFrame.model.ChatRequiredPara;
import com.huaji.galgamebyhuaji.vignaAiFrame.myenum.AiMerchantType;
import reactor.core.publisher.Flux;

public interface VignaHttpClient {
    /**
     * 调用此方法前需要将发送信息整合到上下文中,发送json根据上下文拼接而来
     * 记录发送的json和返回的json到上下文中,返回原始响应json
     */
    String sendAiMsg(ChatRequiredPara para) throws JsonProcessingException;
    
    /**
     * 记录发送的json和返回的json到上下文中,返回原始响应流
     */
    Flux<String> sendAiMsgByStream(ChatRequiredPara para);
    
    AiMerchantType getType();
    
    String getDeftSystemPrompt();
    
    void setKeyServlet(KeyServlet keyServlet);
}
