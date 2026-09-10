package com.huaji.galgamebyhuaji.vignaAiFrame.vo;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
public class VignaMsgTree {
    private String id;
    private OffsetDateTime timestamp;
    private String parentId;
    private List<String> editIds;
    private List<String> retryIds;
}
