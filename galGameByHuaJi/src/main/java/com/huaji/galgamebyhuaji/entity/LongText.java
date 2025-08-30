package com.huaji.galgamebyhuaji.entity;

public class LongText {
    private Integer textId;

    private String title;

    private String longtext;

    public Integer getTextId() {
        return textId;
    }

    public void setTextId(Integer textId) {
        this.textId = textId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getLongtext() {
        return longtext;
    }

    public void setLongtext(String longtext) {
        this.longtext = longtext == null ? null : longtext.trim();
    }
}