package com.huaji.galgamebyhuaji.vignaAiFrame.filter;//package com.huaji.galgamebyhuaji.AOP.ai;

import com.huaji.galgamebyhuaji.vignaAiFrame.ChatContextMap;
import com.huaji.galgamebyhuaji.vignaAiFrame.message.VignaMsg;
import com.huaji.galgamebyhuaji.vignaAiFrame.message.VignaMsgContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j

public class AiRecordAdvisor implements MyBaseAdvisor {
	/**
	 * ai 收发全量日志
	 */
	private static final org.slf4j.Logger aiMsgLog = org.slf4j.LoggerFactory.getLogger("aiChatAllMsg");
	
	/**
	 * 排序生效时,排序越小越前
	 */
	@Override
	public int getIndex () {
		return 1;
	}
	
	/**
	 * 请求发送前的前置方法
	 *
	 * @param sessionId
	 */
	@Override
	public void beforeAdvise (String sessionId) {
	
	}
	
	/**
	 * 请求完全结束时的回调
	 *
	 * @param sessionId
	 */
	@Override
	public void afterAdvise (String sessionId) {
	
	}
	
	private void saveUserMsg (String sessionId) {
		VignaMsgContext context = ChatContextMap.consumptionContext(sessionId);
		if ( context == null ) {
			log.warn("未找到 context,跳过用户消息记录");
			return;
		}
		VignaMsg content = context.getContent();
		
	}
}
//                requestJson = objectMapper.writeValueAsString(request.prompt());
//            } catch (Exception e) {
//                requestJson = "{\"error\":\"序列化失败\"}";
//            }
//
//            long id = aiChatMsgService.installData(record);
//            if (id <= 0) throw new WriteError(1, 0);
//            saveData(request, "userRecordId", id);
//
//            // 记录 aiChatAllMsg 日志
//            aiMsgLog.info("[用户消息] userId={}, sessionId={}, index={}, content={}",
//                          param.getUserId(), record.getSessionId(), param.getIndex(), param.getUserContent());
//        } catch (Exception e) {
//            log.error("保存用户消息失败", e);
//            throw new OperationException("用户发送信息保存失败!请稍后重试");
//        }
//    }
//
//    private ChatClientResponse saveAiMsg(ChatClientResponse response) {
//        Object rawParam = getParam(response, AiChatClientParam.PARAM_KEY);
//        if (!(rawParam instanceof AiChatClientParam param)) {
//            log.warn("未找到 AiChatClientParam，跳过AI消息记录");
//            return response;
//        }
//        String aiContent = null;
//        try {
//            if (response.chatResponse() != null && response.chatResponse().getResult() != null) {
//                aiContent = response.chatResponse().getResult().getOutput().getText();
//            }
//            log.debug("ai返回内容============>>{}", aiContent);
//        } catch (Exception e) {
//            log.error("获取 AI 文本失败", e);
//        }
//        if (MyStringUtil.isNull(aiContent)) aiContent = "AI响应为空";
//
//        // 序列化响应对象作为原始 JSON
//        String responseJson;
//        try {
//            responseJson = objectMapper.writeValueAsString(response);
//        } catch (Exception e) {
//            responseJson = "{\"error\":\"序列化失败\"}";
//        }
//
//        AiRecordWithBLOBs record = new AiRecordWithBLOBs();
//        record.setContent(aiContent);
//        record.setPromptContent(param.getPromptContent());
//        record.setRequestJson(responseJson);
//        record.setChatIndex(param.getIndex() + 1);
//        record.setUserId(param.getUserId());
//        record.setRole(MsgType.AI_MSG.getType());
//        record.setSessionId(param.getSessionId());
//        aiChatMsgService.installData(record);
//
//        // 记录 aiChatAllMsg 日志
//        aiMsgLog.info("[AI回复] userId={}, sessionId={}, index={}, content={}",
//                      param.getUserId(), param.getSessionId(), param.getIndex() + 1, aiContent);
//
//        return response;
//    }