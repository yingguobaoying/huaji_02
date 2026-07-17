package com.huaji.galgamebyhuaji.AOP.ai;

import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.model.AiChatClientParam;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

public class MyBaseAdvisor implements BaseAdvisor {
    protected AiChatClientParam getParam(ChatClientRequest request) {
        Object param = request.context().get(AiChatClientParam.PARAM_KEY);
        if (!(param instanceof AiChatClientParam))
            throw new OperationException("消息参数传递错误请稍后重试");
        return (AiChatClientParam) param;
    }
    
    protected AiChatClientParam getParam(ChatClientResponse r) {
        Object param = r.context().get(AiChatClientParam.PARAM_KEY);
        if (!(param instanceof AiChatClientParam))
            throw new OperationException("消息参数传递错误请稍后重试");
        return (AiChatClientParam) param;
    }
    
    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        return BaseAdvisor.super.adviseCall(chatClientRequest, callAdvisorChain);
    }
    
    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        return BaseAdvisor.super.adviseStream(chatClientRequest, streamAdvisorChain);
    }
    
    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        return null;
    }
    
    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        return null;
    }
    
    @Override
    public Scheduler getScheduler() {
        return BaseAdvisor.super.getScheduler();
    }
    
    @Override
    public int getOrder() {
        return 0;
    }
}
