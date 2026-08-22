//package com.huaji.galgamebyhuaji.service.ai;
//
//import com.huaji.galgamebyhuaji.entity.AiClientConfigWithBLOBs;
//import com.huaji.galgamebyhuaji.entity.AiRecordWithBLOBs;
//import com.huaji.galgamebyhuaji.model.AiChatClientParam;
//import com.huaji.galgamebyhuaji.model.ReturnResult;
//import reactor.core.publisher.Flux;
//
//import java.util.List;
//import java.util.Map;
//
//public interface AiBastService {
//    //获取配置列表
//    ReturnResult<AiClientConfigWithBLOBs> getList();
//
//    //增加
//    ReturnResult<AiClientConfigWithBLOBs> add(AiClientConfigWithBLOBs aiClientConfig, String apiKey);
//
//    //修改
//    ReturnResult<AiClientConfigWithBLOBs> update(AiClientConfigWithBLOBs aiClientConfig, String apiKey);
//
//    //修改状态
//    ReturnResult<Void> updateState(Long id, boolean newState);
//
//    ReturnResult<Map<String, Integer>> getAiMerchantType();
//
//    /**
//     * ai总结聊天记录
//     */
//    ReturnResult<String> sumUpRecorder(List<AiRecordWithBLOBs> messageList);
//
//
//    /**
//     * 使用AI客户端进行聊天,优先级id>代码>AiChatClientParam设置,二者不可同时为空
//     */
//    ReturnResult<String> aiChat(Long clientId, String code, AiChatClientParam param);
//
//    /**
//     * 可配置的AI聊天\(非流)
//     */
//    ReturnResult<String> aiChat(Long clientId, String code, AiChatClientParam param, AiClientConfigWithBLOBs config);
//
//    /**
//     * 使用AI客户端进行聊天(流式)
//     */
//    Flux<String> aiChatByStream(Long clientId, String code, AiChatClientParam param);
//
//    /**
//     * 使用AI客户端进行聊天(流式)
//     */
//    Flux<String> aiChatByStream(Long clientId, String code, AiChatClientParam param, AiClientConfigWithBLOBs config);
//}