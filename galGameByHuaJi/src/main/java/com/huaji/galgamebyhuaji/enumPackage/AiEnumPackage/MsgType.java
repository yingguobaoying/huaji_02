package com.huaji.galgamebyhuaji.enumPackage.AiEnumPackage;

import lombok.Getter;

@Getter
public enum MsgType {
    // 消息角色：1-用户，2-系统（忽略），3-AI回复，4-上下文总结
    USER("用户消息", 1),
    SYSTEM("系统消息", 2),
    AI_MSG("ai回复", 3),
    SUMMARY("总结上下文",4)
    ;
    
    private final String name;
    private final int type;
    
    MsgType(String name, int type) {
        this.name = name;
        this.type = type;
    }
    
    public static MsgType getType(Integer type) {
        if (type == null) return USER;
        for (MsgType m : MsgType.values()) {
            if (m.getType() == type)
                return m;
        }
        return USER;
    }
    
}
