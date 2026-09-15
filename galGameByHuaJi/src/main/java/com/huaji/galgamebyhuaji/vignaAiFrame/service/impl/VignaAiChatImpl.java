package com.huaji.galgamebyhuaji.vignaAiFrame.service.impl;

import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.vignaAiFrame.ChatContextMap;
import com.huaji.galgamebyhuaji.vignaAiFrame.config.VignaChatClientConfig;
import com.huaji.galgamebyhuaji.vignaAiFrame.message.VignaMsg;
import com.huaji.galgamebyhuaji.vignaAiFrame.message.VignaMsgContext;
import com.huaji.galgamebyhuaji.vignaAiFrame.model.ChatRequiredPara;
import com.huaji.galgamebyhuaji.vignaAiFrame.model.ChatServicePara;
import com.huaji.galgamebyhuaji.vignaAiFrame.myenum.VignaRole;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.VignaAiChat;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.VignaHttpClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@Slf4j
@RequiredArgsConstructor
public class VignaAiChatImpl implements VignaAiChat {
    
    @Override
    public ReturnResult<String> vignaAiChat(ChatServicePara para) {
        Long useClientId =
                para.getClientId() == null ?
                        VignaChatClientConfig.getChatClientId(para.getCode()) : para.getClientId();
        para.setClientId(useClientId);
        para.setCode(null);//避免污染
        VignaMsgContext context;
        VignaHttpClient chatClient = VignaChatClientConfig.getChatClient(para.getClientId());
        if(chatClient==null)
            throw new OperationException("发送失败,因为请求发送器获取失败!");
        if (para.isSumUp()) {
            //如果为总结调用那么会话上下文存在
            context = ChatContextMap.getContext(para.getSessionId());
            if (context == null)
                return ReturnResult.isFalse("总结失败,因为上下文已被清除");
            context.setSum(true);
        } else {
            //普通情况下需要建立上下文
            context = new VignaMsgContext();
            context.setSessionId(para.getSessionId());
            context.setClientId(para.getClientId());
            context.setUserId(para.getUserId());
            context.setContent(para.getMsg());
            context.setMsgId(para.getMsgId());
            context.setType(para.getType());
            context.setSum(false);
        }
        //装配系统消息
        VignaMsg sysMsg = new VignaMsg();
        sysMsg.setRole(VignaRole.system);
        if (para.getConfig() == null || MyStringUtil.isNull(para.getConfig().getContent())) {
            sysMsg.setContent(chatClient.getDeftSystemPrompt());
        } else sysMsg.setContent(para.getConfig().getContent());
        context.setSystemMsg(sysMsg);
        ChatContextMap.setContext(para.getSessionId(), context);
        ChatRequiredPara chatRequiredPara = new ChatRequiredPara();
        chatRequiredPara.setSessionId(para.getSessionId());
        chatRequiredPara.setSumUp(para.isSumUp());
        chatRequiredPara.setExtraJson(para.getExtraJson());
        chatRequiredPara.setConfig(para.getConfig());
        try {
            String s = chatClient.sendAiMsg(chatRequiredPara);
            return ReturnResult.isTrue("ai请求成功", s);
        } catch (Exception e) {
            if (e instanceof OperationException o)
                return ReturnResult.isError("系统出现错误:" + e.getMessage() + "已经终止了请求!");
            return ReturnResult.isFalse("AI请求出错:" + e.getMessage());
        } finally {
            if (!para.isSumUp())
                ChatContextMap.delContext(para.getSessionId());
        }
    }
    
    
    @Override
    public Flux<String> vignaAiChatByStream(ChatServicePara para) {
        //流式请求与普通请求一样需要先建立上下文
        VignaMsgContext context = new VignaMsgContext();
        context.setSessionId(para.getSessionId());
        context.setClientId(para.getClientId());
        context.setUserId(para.getUserId());
        context.setContent(para.getMsg());
        context.setSum(false);
        ChatContextMap.setContext(para.getSessionId(), context);
        VignaHttpClient chatClient = VignaChatClientConfig.getChatClient(para.getClientId());
        if (chatClient == null)
            return Flux.error(new OperationException("AI客户端不存在, clientId: " + para.getClientId()));
        ChatRequiredPara chatRequiredPara = new ChatRequiredPara();
        chatRequiredPara.setSessionId(para.getSessionId());
        chatRequiredPara.setSumUp(false);
        return chatClient.sendAiMsgByStream(chatRequiredPara);
    }
}
