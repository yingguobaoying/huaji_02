package com.huaji.galgamebyhuaji.AOP.ai;

import com.huaji.galgamebyhuaji.dao.AiRecordMapper;
import com.huaji.galgamebyhuaji.entity.AiRecordWithBLOBs;
import com.huaji.galgamebyhuaji.enumPackage.AiEnumPackage.MsgType;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.exceptions.WriteError;
import com.huaji.galgamebyhuaji.model.AiChatClientParam;
import com.huaji.galgamebyhuaji.myUtil.IdUtil;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.publisher.Flux;

import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class AiRecordAdvisor extends MyBaseAdvisor {
    
    private final AiRecordMapper aiRecordMapper;
    private final TransactionTemplate transactionTemplate;
    
    /**
     * 保存用户消息到数据库（在请求发送前）
     * index 由调用方 (aiChat/aiChatByStream) 预先设置好
     */
    private void saveUserMsg(ChatClientRequest request) {
        try {
            Object rawParam = getParam(request, AiChatClientParam.PARAM_KEY);
            if (!(rawParam instanceof AiChatClientParam param)) {
                log.warn("未找到 AiChatClientParam，跳过用户消息记录");
                return;
            }
            int index = param.getIndex();
            transactionTemplate.execute(status -> {
                AiRecordWithBLOBs record = new AiRecordWithBLOBs();
                record.setContent(param.getUserContent());
                record.setPromptContent(param.getPromptContent());
                record.setChatIndex(index);
                record.setUserId(param.getUserId());
                record.setCreatedAt(new Date());
                record.setRole(param.isSumUp() ? MsgType.SUMMARY.getType() : MsgType.USER.getType());
                record.setSessionId(MyStringUtil.isNull(param.getSessionId()) ?
                                            IdUtil.getRandomId("ai_chat_") : param.getSessionId());
                aiRecordMapper.insertSelective(record);
                if (record.getId() == null)
                    WriteError.tryWrite(0);
                saveData(request, "userRecordId", record.getId());
                return null;
            });
        } catch (Exception e) {
            log.error("保存用户消息失败", e);
            throw new OperationException("用户发送信息保存失败!请稍后重试");
        }
    }
    
    /**
     * 保存 AI 回复消息到数据库（在响应返回后）
     */
    private ChatClientResponse saveAiMsg(ChatClientResponse response) {
        
        Object rawParam = getParam(response, AiChatClientParam.PARAM_KEY);
        if (!(rawParam instanceof AiChatClientParam param)) {
            log.error("未找到 AiChatClientParam，跳过AI消息记录");
            if (response.chatResponse() != null) {
                log.info("无参数ai响应记录:{}", response.chatResponse().getResult().getOutput().toString());
            }
            return response;
        }
        
        String aiContent = null;
        if (response.chatResponse() != null && response.chatResponse().getResult() != null) {
            aiContent = response.chatResponse().getResult().getOutput().getText();
        }
        if (aiContent == null) {
            aiContent = "AI响应为空";
        }
        
        writeRecord(param, aiContent, transactionTemplate, aiRecordMapper);
        return response;
    }
    
    public static void writeRecord(AiChatClientParam param, String aiContent, TransactionTemplate transactionTemplate, AiRecordMapper aiRecordMapper) {
        try {
            final String finalAiContent = aiContent;
            transactionTemplate.execute(status -> {
                AiRecordWithBLOBs record = new AiRecordWithBLOBs();
                record.setContent(finalAiContent);
                record.setPromptContent(param.getPromptContent());
                record.setRequestJson(finalAiContent);
                record.setChatIndex(param.getIndex() + 1);
                record.setUserId(param.getUserId());
                record.setCreatedAt(new Date());
                record.setRole(MsgType.AI_MSG.getType());
                record.setSessionId(param.getSessionId());
                aiRecordMapper.insertSelective(record);
                return null;
            });
        } catch (Exception e) {
            log.error("保存AI消息失败", e);
            log.error("信息内容:{},所属用户:{},所属会话:{},发送客户端:{}",
                      aiContent, param.getUserId(), param.getSessionId(), param.getClientId());
        }
    }
    
    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        saveUserMsg(request);
        ChatClientResponse response = chain.nextCall(request);
        return saveAiMsg(response);
    }
    
    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest request, StreamAdvisorChain chain) {
        saveUserMsg(request);
        return chain.nextStream(request);
    }
    
    @Override
    public int getOrder() {
        return 1;
    }
}
