package com.huaji.galgamebyhuaji.entity;

public class LinksWithBLOBs extends Links {
    private String link;

    private String notes;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link == null ? null : link.trim();
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes == null ? null : notes.trim();
    }
}