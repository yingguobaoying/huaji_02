package com.huaji.galgamebyhuaji.vignaAiFrame.service.impl;

import com.huaji.galgamebyhuaji.entity.AiClientConfigWithBLOBs;
import com.huaji.galgamebyhuaji.vignaAiFrame.message.VignaMsg;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.AiClassificationServlet;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.UserWithVignaChat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserWithVignaChatImpl implements UserWithVignaChat {
    private final AiClassificationServlet classificationServlet;
    @Override
    public List<AiClientConfigWithBLOBs> getList(int userId) {
        return List.of();
    }
    
    @Override
    public Map<String, List<VignaMsg>> getUserSession(int userId) {
        return Map.of();
    }
}
