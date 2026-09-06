package com.huaji.galgamebyhuaji.vignaAiFrame.service.impl;//package com.huaji.galgamebyhuaji.service.ai.impl;

import com.huaji.galgamebyhuaji.dao.AiClassificationMapper;
import com.huaji.galgamebyhuaji.dao.AiClientConfigMapper;
import com.huaji.galgamebyhuaji.entity.AiClassification;
import com.huaji.galgamebyhuaji.entity.AiClientConfigExample;
import com.huaji.galgamebyhuaji.entity.AiClientConfigWithBLOBs;
import com.huaji.galgamebyhuaji.exceptions.WriteError;
import com.huaji.galgamebyhuaji.myUtil.ListUtil;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.AiClassificationServlet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiClassificationServletImpl implements AiClassificationServlet {
    private final AiClassificationMapper classificationMapper;
    private final RedisCacheManager cacheManager;
    private final AiClientConfigMapper configMapper;
    
    @Override
    @Cacheable(key = "#userId + ':' + #configId",
            cacheNames = AiClassificationServlet.user_view_cache_key,
            unless = "#result == false")   // 只缓存 true 的结果，false 不缓存（避免缓存穿透）
    public boolean userCanSee(int userId, long configId) {
        int b = classificationMapper.userCanSee(userId, configId);
        return b > 0;
    }
    
    @Override//新增不会影响已有缓存,保留
    public void addDate(AiClassification classification) {
        classification.setId(null);
        WriteError.tryWrite(classificationMapper.insert(classification));
    }
    
    @Override
    public void delDate(Long classificationId) {
        AiClassification aiClassification = classificationMapper.selectByPrimaryKey(classificationId);
        long config = aiClassification.getClientId();
        // 查出所有拥有该权限的用户
        List<Integer> userIds = classificationMapper.getUserIdsByConfigId(config);
        //从缓存管理器中获取目标缓存
        Cache cache = cacheManager.getCache(AiClassificationServlet.user_view_cache_key);
        //逐个删除对应的缓存 key
        if (cache != null && !ListUtil.isNull(userIds))
            for (Integer userId : userIds)
                cache.evict(userId + ":" + config);
        WriteError.tryWrite(classificationMapper.deleteByPrimaryKey(classificationId));
    }
    
    public List<AiClientConfigWithBLOBs> getList(int userId) {
        List<AiClassification> userViewList = classificationMapper.getUserViewList(userId);
        if (ListUtil.isNull(userViewList))
            return List.of();
        List<Long> configId = userViewList.stream().map(AiClassification::getClientId).toList();
        return configMapper.selectByIds(configId);
    }
}
