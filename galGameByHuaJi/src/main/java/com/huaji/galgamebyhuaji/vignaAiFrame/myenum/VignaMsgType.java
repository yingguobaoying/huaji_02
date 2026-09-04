package com.huaji.galgamebyhuaji.vignaAiFrame.myenum;

import lombok.Getter;

@Getter
public enum VignaMsgType {
    generic(1, "普通对话"),
    edit(2, "修改对话"),
    retry(3, "重试对话"),
    ;
    private final int code;
    private final String name;
    
    VignaMsgType(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
