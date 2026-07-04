package com.huaji.galgamebyhuaji.entity;

public class AiClientConfigWithBLOBs extends AiClientConfig {
    private String baseUrl;

    private String apiKey;

    private String extraConfigJson;

    private String content;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl == null ? null : baseUrl.trim();
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey == null ? null : apiKey.trim();
    }

    public String getExtraConfigJson() {
        return extraConfigJson;
    }

    public void setExtraConfigJson(String extraConfigJson) {
        this.extraConfigJson = extraConfigJson == null ? null : extraConfigJson.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }
}