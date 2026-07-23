package com.huaji.galgamebyhuaji.vo;

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
    /**
     * 配置编码
     */
    private String code;
}
