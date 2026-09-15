package com.huaji.galgamebyhuaji.vignaAiFrame.message;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.huaji.galgamebyhuaji.myUtil.ObjectUtil;
import com.huaji.galgamebyhuaji.vignaAiFrame.myenum.VignaRole;
import com.huaji.galgamebyhuaji.vignaAiFrame.node.VignaMessageNode;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * AI消息类
 */
@Getter
@Setter
public class VignaMsg {
    /**
     * 本消息角色身份
     */
    private VignaRole role;
    /**
     * 本条消息内容
     */
    private String content;
    /**
     * 属于对话下标
     */
    private int index;
    /**
     * 可选，tool 调用时使用,这两玩意暂时不打算支持先撂在这里
     */
    private String name;
    private List<VignaTool> tools;
    private OffsetDateTime timestamp;
    
    public VignaMsg() {
    }
    
    public VignaMsg(VignaMessageNode m) {
        setContent(m.getContent());
        setRole(VignaRole.getType(m.getRole()));
        setIndex(m.getTurnIndex());
        setTimestamp(m.getTimestamp());
    }
    
    
    public ObjectNode getJson() {
        ObjectNode root = ObjectUtil.getObjectMapper().createObjectNode();
        root.put("role", role.getValue());
        if (role == VignaRole.system)
            root.put("content", content);
        else {
            if (timestamp == null) timestamp = OffsetDateTime.now();
            root.put("content", content + "\n[system output]:此信息发送于：%s".formatted(timestamp));
        }
        return root;
    }
    //占位方法
    public ObjectNode getToolJson(){
        return null;
    }
}
