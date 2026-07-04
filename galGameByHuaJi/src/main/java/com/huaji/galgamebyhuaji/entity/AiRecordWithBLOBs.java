package com.huaji.galgamebyhuaji.entity;

public class AiRecordWithBLOBs extends AiRecord {
    private String content;

    private String promptContent;

    private String requestJson;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getPromptContent() {
        return promptContent;
    }

    public void setPromptContent(String promptContent) {
        this.promptContent = promptContent == null ? null : promptContent.trim();
    }

    public String getRequestJson() {
        return requestJson;
    }

    public void setRequestJson(String requestJson) {
        this.requestJson = requestJson == null ? null : requestJson.trim();
    }
}