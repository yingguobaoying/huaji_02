package com.huaji.galgamebyhuaji.vignaAiFrame.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huaji.galgamebyhuaji.entity.AiClientConfigWithBLOBs;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.vignaAiFrame.ChatContextMap;
import com.huaji.galgamebyhuaji.vignaAiFrame.config.VignaChatClientConfig;
import com.huaji.galgamebyhuaji.vignaAiFrame.constant.AiConstant;
import com.huaji.galgamebyhuaji.vignaAiFrame.constant.AiPromptTemplate;
import com.huaji.galgamebyhuaji.vignaAiFrame.message.VignaMsg;
import com.huaji.galgamebyhuaji.vignaAiFrame.message.VignaMsgContext;
import com.huaji.galgamebyhuaji.vignaAiFrame.model.ChatServicePara;
import com.huaji.galgamebyhuaji.vignaAiFrame.myenum.VignaMsgType;
import com.huaji.galgamebyhuaji.vignaAiFrame.myenum.VignaRole;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.VignaAiChat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * 上下文压缩器,自动检查传入的上下文是否需要压缩执行位置为倒数第二
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class VignaChatZipAdvisor implements MyBaseAdvisor {
    @Value("${ai.chat-record-size}")
    private int size;
    @Value("${ai.chat-token-size}")
    private int maxTokenSize;
    private final VignaAiChat vignaChat;
    private final ObjectMapper objectMapper;
    
    @Override
    public int getIndex() {
        return 10;
    }
    
    @Override
    public void beforeAdvise(String sessionId) {
        VignaMsgContext context = ChatContextMap.getContext(sessionId);
        if (context == null) throw new OperationException("请求上下文不存在，可能已被提前清理");
        List<VignaMsg> historyMsgList = context.getHistoryMsgList();
        //理论上记录应该是按时间排序的,但是这里保险一点重新排一次
        VignaMsg userMsg = context.getContent();
        if (historyMsgList.isEmpty()) {
            userMsg.setIndex(1);//没有就不管它,直接设置消息下标
            return;
        }
        historyMsgList.sort(Comparator.comparingInt(VignaMsg::getIndex));
        //获取统计信息并整理下标
        int maxIndex = -114514, textSize = 0;
        for (VignaMsg vignaMsg : historyMsgList) {
            if (vignaMsg.getIndex() > maxIndex)
                maxIndex = vignaMsg.getIndex();
            textSize += vignaMsg.getContent().length();
        }
        //触发压缩的条件:文本或者对话轮数达到阈值并且自身不为压缩请求,同时此消息为普通消息(类型为普通或者null)
        boolean needSum =
                (maxTokenSize <= textSize || size <= historyMsgList.size())
                && !(context.isSum()) && (context.getType() == null || context.getType() == VignaMsgType.generic);
        VignaMsg sysMsg = context.getSystemMsg();
        if (needSum) {//需要压缩的情况
            ChatServicePara para = new ChatServicePara();
            para.setSumUp(true);
            para.setSessionId(sessionId);
            para.setMsgId(context.getMsgId());
            if (VignaChatClientConfig.codeState(AiConstant.AI_SUM)) {
                para.setCode(AiConstant.AI_SUM);//这里使用默认的AI提示词
                context.setSystemMsg(null);
            } else {
                para.setClientId(context.getClientId());
                AiClientConfigWithBLOBs c = new AiClientConfigWithBLOBs();
                c.setContent(AiPromptTemplate.CHAT_SUMMARY_PROMPT);
                para.setConfig(c);
                VignaMsg vignaMsg = new VignaMsg();
                vignaMsg.setRole(VignaRole.system);
                vignaMsg.setContent(AiPromptTemplate.CHAT_SUMMARY_PROMPT);
                context.setSystemMsg(vignaMsg);
            }
            //设置用户提示内容,避免ai因为长上下文导致的错误回复
            VignaMsg msg = new VignaMsg();
            msg.setRole(VignaRole.user);
            msg.setContent("[system output]: 请按照提示词设置,客观的总结以上传入的上下文信息(请勿带入对话),并且在总结的时候需要忽略此消息,以保证用户体验和之后的AI总结不会出错");
            context.setContent(msg);
            ChatContextMap.setContext(sessionId, context);
            log.info("会话{}上下文窗口压缩流程准备开始", sessionId);
            ReturnResult<String> zipReturnResult = vignaChat.vignaAiChat(para);
            log.info("会话{}上下文窗口压缩完成,结果{}", sessionId, zipReturnResult.isOperationResult() ? "成功" : "失败");
            if (zipReturnResult.isOperationResult()) {
                VignaMsg returnMst = new VignaMsg();
                //正常总结完成后下标+2(请求发送+1,ai回复+1)
                //总结xx-x,请求提示词->x+1,ai回复->x+2,此信息->x+3,ai对此信息的回复->x+4
                context = ChatContextMap.getContext(sessionId);//重新获取上下文避免jvm拿缓存
                returnMst.setIndex(userMsg.getIndex() + 2);//请求提示词跨了一个请求消息
                userMsg.setIndex(returnMst.getIndex() + 1);//
                returnMst.setContent("[system Summary of Chat records]:" + zipReturnResult.getReturnResult());
                returnMst.setRole(VignaRole.sum);
                context.getHistoryMsgList().add(returnMst);
                context.setContent(userMsg);
                context.setSum(false);
                context.setSendTime(null);
                context.setTrySize(0);//更新内容
            } else {
                log.error("上下文压缩时出现错误:{}跳过了此次自动压缩", zipReturnResult.getMsg());
                if (zipReturnResult.isHasError())
                    throw new OperationException(zipReturnResult.getMsg());
            }
        } else {//普通情况
            if (historyMsgList.isEmpty())
                userMsg.setIndex(1);
            else
                userMsg.setIndex(maxIndex + 1);
        }
        context.setContent(userMsg);
        context.setSystemMsg(sysMsg);
        ChatContextMap.setContext(sessionId, context);
    }
    
    @Override
    public void afterAdvise(String sessionId) {
        VignaMsgContext context = ChatContextMap.getContext(sessionId);
        if (context == null) throw new OperationException("请求上下文不存在，可能已被提前清理");
        //计算回复信息的下标
        VignaMsg reply = context.getAiReply();
        if(reply==null){
            log.warn("ai请求返回空体!已跳过下标计算");
            return;
        }
        reply.setIndex(context.getContent().getIndex() + 1);
        context.setAiReply(reply);
    }
}
