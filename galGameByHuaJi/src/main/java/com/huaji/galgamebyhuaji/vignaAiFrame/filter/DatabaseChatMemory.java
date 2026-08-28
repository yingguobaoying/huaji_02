package com.huaji.galgamebyhuaji.vignaAiFrame.filter;//package com.huaji.galgamebyhuaji.AOP.ai;

import com.huaji.galgamebyhuaji.entity.AiRecord;
import com.huaji.galgamebyhuaji.entity.AiRecordWithBLOBs;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.vignaAiFrame.ChatContextMap;
import com.huaji.galgamebyhuaji.vignaAiFrame.message.VignaMsg;
import com.huaji.galgamebyhuaji.vignaAiFrame.message.VignaMsgContext;
import com.huaji.galgamebyhuaji.vignaAiFrame.myenum.VignaRole;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.AiChatMsgService;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.VignaAiChat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 获取聊天记录的切面
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DatabaseChatMemory implements MyBaseAdvisor {
    @Value("${ai.chat-record-size}")
    private int chatRecordSize;
    private final AiChatMsgService chatMsgService;
    private final VignaAiChat chatService;
    
    /**
     * 排序生效时,排序越小越前
     */
    @Override
    public int getIndex() {
        return 1;
    }
    
    /**
     * 请求发送前的前置方法
     *
     * @param sessionId
     */
    @Override
    public void beforeAdvise(String sessionId) {
        VignaMsgContext context = ChatContextMap.getContext(sessionId);
        if (context == null) throw new OperationException("请求上下文不存在，可能已被提前清理");
        //整合历史记录
        List<AiRecordWithBLOBs> lastRecord = chatMsgService.getRecord(sessionId, context.getUserId());
        boolean needSumUp = lastRecord.size() >= chatRecordSize && !context.isSum();
        // 达到保留轮数时触发总结,并且当前消息不是总结接口调用的
        lastRecord.sort(Comparator.comparingInt(AiRecord::getChatIndex));
        List<VignaMsg> type = new ArrayList<>(lastRecord.size());
        for (int i = lastRecord.size() - 1, index = 0; i >= 0; i--, index++) {//从最后一个开始计数
            AiRecordWithBLOBs record = lastRecord.get(i);
            VignaMsg msg = new VignaMsg();
            VignaRole role = VignaRole.getType(record.getRole());
            boolean isSum = VignaRole.sum == role;
            msg.setRole(role);
            msg.setContent(record.getContent());
            msg.setIndex(index);
            type.add(msg);
            //当下标大于历史记录条数且无总结消息时触发总结
            if (index < chatRecordSize || !needSumUp) {
                if (index >= chatRecordSize) break;
            }
            if (isSum) break;//遇到上次总结内容时立刻终止
        }//拿到用户消息并进行下标管理
        VignaMsg userMsg = context.getContent();
        if (needSumUp) {
            log.info("会话{}开始进行上下文压缩", sessionId);
            //拿到了需要总结的列表
            context.setHistoryMsgList(type);
            context.setSum(true);
            //更新上下文
            ChatContextMap.setContext(sessionId, context);
            //进行总结
            log.info("会话{}准备开始进行上下文窗口压缩", sessionId);
            ReturnResult<String> stringReturnResult = chatService.vignaAiChat(null, sessionId, context.getUserId(), context.getUserId(), true);
            log.info("会话{}上下文窗口压缩完成,结果{}", sessionId, stringReturnResult.isOperationResult() ? "成功" : "失败");
            if (stringReturnResult.isOperationResult()) {
                VignaMsg msg = new VignaMsg();
                //正常总结完成后下标+2(请求发送+1,ai回复+1)
                //总结1-4,请求提示词->5,ai回复->6,此信息->7,ai对此信息的回复->8
                msg.setIndex(lastRecord.getLast().getChatIndex() + 2);
                userMsg.setIndex(msg.getIndex() + 1);
                msg.setContent("[System Summary]:" + stringReturnResult.getReturnResult());
                msg.setRole(VignaRole.sum);
                type.add(msg);
                context.setContent(userMsg);
                context.setSum(false);
                context.setSendTime(null);
                context.setTrySize(0);//更新内容
            } else {
                log.error("上下文压缩时出现错误:{}跳过了此次自动压缩", stringReturnResult.getMsg());
                if (stringReturnResult.isHasError())
                    throw new OperationException(stringReturnResult.getMsg());
            }
        } else {
            if (lastRecord.isEmpty())
                userMsg.setIndex(1);
            else//获取历史记录中最大的下标+1
                userMsg.setIndex(lastRecord.getLast().getChatIndex() + 1);
        }
        //重新排序
        type.sort(Comparator.comparingInt(VignaMsg::getIndex));
        //记录用户消息
        context.setHistoryMsgList(type);//更新上下文
        context.setContent(userMsg);
        ChatContextMap.setContext(sessionId, context);//整理完成
    }
    
    /**
     * 请求完全结束时的回调
     *
     * @param sessionId
     */
    @Override
    public void afterAdvise(String sessionId) {
        VignaMsgContext context = ChatContextMap.getContext(sessionId);
        if (context == null) throw new OperationException("请求上下文不存在，可能已被提前清理");
        if (context.isSum()) {
            VignaMsg reply = context.getAiReply();
            reply.setContent("[System Summary]:" + reply.getContent());
            reply.setRole(VignaRole.sum);
        }
    }
}
