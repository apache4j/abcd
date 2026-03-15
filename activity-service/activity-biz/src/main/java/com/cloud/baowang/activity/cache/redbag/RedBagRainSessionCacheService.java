package com.cloud.baowang.activity.cache.redbag;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.cloud.baowang.activity.po.SiteActivityRedBagSessionPO;
import com.cloud.baowang.activity.repositories.SiteActivityRedBagSessionRepository;
import com.cloud.baowang.common.core.constants.CacheConstants;
import com.github.jesse.l2cache.spring.biz.AbstractCacheService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 红包雨活动缓存组建
 */
@Component
@AllArgsConstructor
public class RedBagRainSessionCacheService extends AbstractCacheService<String, SiteActivityRedBagSessionPO> {
    private SiteActivityRedBagSessionRepository sessionRepository;
    @Override
    public String getCacheName() {
        return CacheConstants.ACTIVITY_REDBAG_RAIN_SESSION_CACHE;
    }

    @Override
    public SiteActivityRedBagSessionPO queryData(String key) {
        return new LambdaQueryChainWrapper<>(sessionRepository)
                .eq(SiteActivityRedBagSessionPO::getSessionId, key)
                .one();
    }

    @Override
    public Map<String, SiteActivityRedBagSessionPO> queryDataList(List<String> keyList) {
        List<SiteActivityRedBagSessionPO> list = new LambdaQueryChainWrapper<>(sessionRepository)
                .in(SiteActivityRedBagSessionPO::getSessionId, keyList)
                .list();
        if (CollUtil.isNotEmpty(list)) {
            return list.stream().collect(Collectors.toMap(SiteActivityRedBagSessionPO::getSessionId, p -> p));
        }
        return null;
    }
}
