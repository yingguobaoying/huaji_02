package com.huaji.galgamebyhuaji.vo;

import com.huaji.galgamebyhuaji.entity.AiClientConfigWithBLOBs;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiModerList {
    /**
     * 模型名称
     */
    private String name;
    /**
     * 描述(可能为空)
     */
    private String content;
    /**
     * 配置id
     */
    private long configId;
    
    public AiModerList() {
    }
    
    public AiModerList(AiClientConfigWithBLOBs bloBs) {
        this.name = bloBs.getName();
        this.code = bloBs.getCode();
        this.configId = bloBs.getId();
        this.content = bloBs.getContent();
    }
    
    /**
     * 配置编码
     */
    private String code;
}
