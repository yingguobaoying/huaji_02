package com.huaji.galgamebyhuaji.vignaAiFrame.service;//package com.huaji.galgamebyhuaji.service.ai;

import com.huaji.galgamebyhuaji.vignaAiFrame.message.VignaMsg;
import com.huaji.galgamebyhuaji.vignaAiFrame.node.VignaMessageNode;
import com.huaji.galgamebyhuaji.vignaAiFrame.vo.VignaMsgTree;

import java.util.List;

public interface AiChatMsgService {
    /**
     * 检查这个会话是否存在聊天记录
     */
    boolean hasSession(String sessionId);
    
    /**
     * 获取某个会话的聊天信息
     *
     * @param session 获取的聊天会话的默认链路
     * @param msgId   信息开始的位置,如果不传入返回默认的,这个参数优先生效
     * @param size    包含总结节点的个数
     *
     * @return 返回从传入信息开始向上直到上个有效截断为止的完整的历史记录
     */
    List<VignaMsg> getMsgList(String session, String msgId, int size);
    
    List<VignaMsg> getMsgList(String session, int size);
    
    /**
     * 修改消息
     *
     * @param msgId     编辑的信息id
     * @param sessionId 属于会话
     * @param msg       发送信息
     * @param clientID  使用的配置
     *
     * @return 保存的节点
     */
    VignaMessageNode editData(String msgId, String sessionId, VignaMsg msg, Long clientID);
    
    /**
     * 重试消息
     *
     * @param msgId     重试的信息id
     * @param sessionId 属于会话
     * @param msg       发送信息
     * @param clientID  使用的配置
     *
     * @return 保存的节点
     */
    VignaMessageNode retry(String msgId, String sessionId, VignaMsg msg, Long clientID);
    
    /**
     * 保存消息
     *
     * @param sessionId     属于会话
     * @param msg           发送信息
     * @param updateSession 是否自动更新所属会话的最新消息
     *
     * @return 保存的节点
     */
    VignaMessageNode setData(VignaMessageNode msg, String sessionId, boolean updateSession);
    
    VignaMessageNode getMsgNode(String msgId);
    
    List<VignaMsg> getMsgByIds(List<String> idList);
    
    List<VignaMsgTree> getTree(String sessionId);
}
