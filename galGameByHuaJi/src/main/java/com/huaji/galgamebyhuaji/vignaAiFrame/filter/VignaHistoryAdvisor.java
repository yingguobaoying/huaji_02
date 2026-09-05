package com.huaji.galgamebyhuaji.vignaAiFrame.filter;

import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.vignaAiFrame.ChatContextMap;
import com.huaji.galgamebyhuaji.vignaAiFrame.message.VignaMsg;
import com.huaji.galgamebyhuaji.vignaAiFrame.message.VignaMsgContext;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.AiChatMsgService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 历史记录填充器职责:按照上下文入参提供历史记录,最先执行
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class VignaHistoryAdvisor implements MyBaseAdvisor {
    private final AiChatMsgService msgService;
    @Override
    public int getIndex() {
        return Integer.MIN_VALUE;
    }
    
    @Override
    public void beforeAdvise(String sessionId) {
        VignaMsgContext context = ChatContextMap.getContext(sessionId);
        if (context == null) throw new OperationException("请求上下文不存在，可能已被提前清理");
        if(context.isSum())return;//如果是压缩上下文就不管他
        String msgId = context.getMsgId();
        //获取到上个总结节点为止的记录
        List<VignaMsg> msgList = msgService.getMsgList(sessionId, msgId, 1);
        //不设置系统消息,仅整理消息内容,由外部指定使用特定消息还是覆盖
        context.setHistoryMsgList(msgList);
        ChatContextMap.setContext(sessionId, context);
    }
    
    @Override
    public void afterAdvise(String sessionId) {
    
    }
}
