package com.huaji.galgamebyhuaji.dto;

import java.util.List;

/**
 * @author 滑稽/因果报应
 */
public class SelectMxg {
    private String rType;
    private List<Integer> tags;
    private int size;
    private String rName;

    public String getrType() {
        return rType;
    }

    public void setrType(String rType) {
        this.rType = rType;
    }

    public List<Integer> getTags() {
        return tags;
    }

    public void setTags(List<Integer> tags) {
        this.tags = tags;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getrName() {
        return rName;
    }

    public void setrName(String rName) {
        this.rName = rName;
    }
}