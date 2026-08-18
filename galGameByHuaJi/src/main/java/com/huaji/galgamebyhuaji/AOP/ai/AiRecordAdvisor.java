package com.huaji.galgamebyhuaji.AOP.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.huaji.galgamebyhuaji.entity.AiRecordWithBLOBs;
import com.huaji.galgamebyhuaji.enumPackage.AiEnumPackage.MsgType;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.exceptions.WriteError;
import com.huaji.galgamebyhuaji.model.AiChatClientParam;
import com.huaji.galgamebyhuaji.myUtil.IdUtil;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.service.ai.AiChatMsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AiRecordAdvisor extends MyBaseAdvisor {
    
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    
    /**
     * ai 收发全量日志
     */
    private static final org.slf4j.Logger aiMsgLog = org.slf4j.LoggerFactory.getLogger("aiChatAllMsg");
    
    private final AiChatMsgService aiChatMsgService;
    
    public AiRecordAdvisor(AiChatMsgService aiChatMsgService) {
        this.aiChatMsgService = aiChatMsgService;
    }
    
    private void saveUserMsg(ChatClientRequest request) {
        try {
            Object rawParam = getParam(request, AiChatClientParam.PARAM_KEY);
            if (!(rawParam instanceof AiChatClientParam param)) {
                log.warn("未找到 AiChatClientParam，跳过用户消息记录");
                return;
            }
            // 序列化请求对象作为原始 JSON
            String requestJson;
            try {
                requestJson = objectMapper.writeValueAsString(request.prompt());
            } catch (Exception e) {
                requestJson = "{\"error\":\"序列化失败\"}";
            }
            
            AiRecordWithBLOBs record = new AiRecordWithBLOBs();
            record.setContent(param.getUserContent());
            record.setPromptContent(param.getPromptContent());
            record.setRequestJson(requestJson);
            record.setChatIndex(param.getIndex());
            record.setUserId(param.getUserId());
            record.setRole(param.isSumUp() ? MsgType.SUMMARY.getType() : MsgType.USER.getType());
            record.setSessionId(MyStringUtil.isNull(param.getSessionId())
                                        ? IdUtil.getRandomId("ai_chat_") : param.getSessionId());
            param.setSessionId(record.getSessionId());
            long id = aiChatMsgService.installData(record);
            if (id <= 0) throw new WriteError(1, 0);
            saveData(request, "userRecordId", id);
            
            // 记录 aiChatAllMsg 日志
            aiMsgLog.info("[用户消息] userId={}, sessionId={}, index={}, content={}",
                          param.getUserId(), record.getSessionId(), param.getIndex(), param.getUserContent());
        } catch (Exception e) {
            log.error("保存用户消息失败", e);
            throw new OperationException("用户发送信息保存失败!请稍后重试");
        }
    }
    
    private ChatClientResponse saveAiMsg(ChatClientResponse response) {
        Object rawParam = getParam(response, AiChatClientParam.PARAM_KEY);
        if (!(rawParam instanceof AiChatClientParam param)) {
            log.warn("未找到 AiChatClientParam，跳过AI消息记录");
            return response;
        }
        String aiContent = null;
        try {
            if (response.chatResponse() != null && response.chatResponse().getResult() != null) {
                aiContent = response.chatResponse().getResult().getOutput().getText();
            }
            log.debug("ai返回内容============>>{}", aiContent);
        } catch (Exception e) {
            log.error("获取 AI 文本失败", e);
        }
        if (MyStringUtil.isNull(aiContent)) aiContent = "AI响应为空";
        
        // 序列化响应对象作为原始 JSON
        String responseJson;
        try {
            responseJson = objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            responseJson = "{\"error\":\"序列化失败\"}";
        }
        
        AiRecordWithBLOBs record = new AiRecordWithBLOBs();
        record.setContent(aiContent);
        record.setPromptContent(param.getPromptContent());
        record.setRequestJson(responseJson);
        record.setChatIndex(param.getIndex() + 1);
        record.setUserId(param.getUserId());
        record.setRole(MsgType.AI_MSG.getType());
        record.setSessionId(param.getSessionId());
        aiChatMsgService.installData(record);
        
        // 记录 aiChatAllMsg 日志
        aiMsgLog.info("[AI回复] userId={}, sessionId={}, index={}, content={}",
                      param.getUserId(), param.getSessionId(), param.getIndex() + 1, aiContent);
        
        return response;
    }
    
    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        saveUserMsg(request);
        ChatClientResponse response = chain.nextCall(request);
        return saveAiMsg(response);
    }
    
    @Override
    public reactor.core.publisher.Flux<ChatClientResponse> adviseStream(
            ChatClientRequest request, StreamAdvisorChain chain) {
        saveUserMsg(request);
        return chain.nextStream(request);
    }
    
    @Override
    public int getOrder() {
        return 1;
    }
}