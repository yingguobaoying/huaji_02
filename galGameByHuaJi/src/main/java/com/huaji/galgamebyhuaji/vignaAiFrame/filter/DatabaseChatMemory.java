package com.huaji.galgamebyhuaji.vignaAiFrame.filter;//package com.huaji.galgamebyhuaji.AOP.ai;

import com.huaji.galgamebyhuaji.entity.AiRecord;
import com.huaji.galgamebyhuaji.entity.AiRecordWithBLOBs;
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
	public int getIndex () {
		return 2;
	}
	
	/**
	 * 请求发送前的前置方法
	 *
	 * @param sessionId
	 */
	@Override
	public void beforeAdvise (String sessionId) {
		VignaMsgContext context = ChatContextMap.consumptionContext(sessionId);
		//整合历史记录
		List<AiRecordWithBLOBs> lastRecord = chatMsgService.getRecord(sessionId, context.getUserId());
		boolean needSumUp = lastRecord.size() >= chatRecordSize && !context.isSum();
		// 达到保留轮数时触发总结,并且当前消息不是总结接口调用的
		lastRecord.sort(Comparator.comparingInt(AiRecord::getChatIndex));
		List<VignaMsg> type = new ArrayList<>(lastRecord.size());
		for ( int i = lastRecord.size() - 1, index = 0; i >= 0; i--, index++ ) {//从最后一个开始计数
			AiRecordWithBLOBs record = lastRecord.get(i);
			VignaMsg msg = new VignaMsg();
			VignaRole role = VignaRole.getType(record.getRole());
			boolean isSum = VignaRole.sum == role;
			msg.setRole(role);
			msg.setContent(record.getContent());
			msg.setIndex(index);
			type.add(msg);
			//当下标大于历史记录条数且无总结消息时触发总结
			if ( index < chatRecordSize || !needSumUp ) {
				if ( index >= chatRecordSize ) break;
			}
			if ( isSum ) break;//遇到上次总结内容时立刻终止
		}
		if ( needSumUp ) {
			//拿到了需要总结的列表
			context.setHistoryMsgList(type);
			context.setSum(true);
			//更新上下文
			ChatContextMap.setContext(sessionId,context);
			//拿到当前用户信息
			VignaMsg userMsg = context.getContent();
			//进行总结
			ReturnResult<String> stringReturnResult = chatService.vignaAiChat(null, sessionId, true);
			if ( stringReturnResult.isOperationResult() ) {
				VignaMsg msg = new VignaMsg();
				msg.setIndex(lastRecord.getLast().getChatIndex() + 1);
				msg.setContent("以下为系统总结的前面的内容:" + stringReturnResult.getReturnResult());
				msg.setRole(VignaRole.sum);
				type.add(msg);
				context.setContent(userMsg);
			} else {
				log.error("总结内容时出现错误:{}跳过了此次自动总结", stringReturnResult.getMsg());
			}
		}
		//重新排序
		type.sort(Comparator.comparingInt(VignaMsg::getIndex));
		//记录用户消息
		
	}
	
	/**
	 * 请求完全结束时的回调
	 *
	 * @param sessionId
	 */
	@Override
	public void afterAdvise (String sessionId) {
	
	}
}