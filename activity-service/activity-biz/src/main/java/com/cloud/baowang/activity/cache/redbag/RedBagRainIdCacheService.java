package com.cloud.baowang.activity.cache.redbag;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.cloud.baowang.activity.api.constants.ActivityConstant;
import com.cloud.baowang.activity.api.enums.redbag.RedBagSessionStatusEnum;
import com.cloud.baowang.activity.po.SiteActivityRedBagSessionPO;
import com.cloud.baowang.activity.repositories.SiteActivityRedBagSessionRepository;
import com.cloud.baowang.common.core.constants.CacheConstants;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.github.jesse.l2cache.spring.biz.AbstractCacheService;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 红包雨活动session id 缓存组建
 */
@Component
@AllArgsConstructor
@Slf4j
public class RedBagRainIdCacheService extends AbstractCacheService<String, List<String>> {
    private SiteActivityRedBagSessionRepository sessionRepository;

    @Override
    public String getCacheName() {
        return CacheConstants.ACTIVITY_REDBAG_RAIN_SESSION_ID_CACHE;
    }

    @Override
    public List<String> queryData(String key) {
        // key == progressing
        String[] split = key.split(CommonConstant.COLON, 2);
        String siteCode = split[0];
        String type = split[1];
        log.info("根据站点:{},类型:{}查询红包活动",siteCode,type);
        if (StrUtil.isNotBlank(type)) {
            if (type.equals(ActivityConstant.ACTIVITY_REDBAG_REAL_TIME_SESSION_KEY)) {
                SiteActivityRedBagSessionPO sessionPO = new LambdaQueryChainWrapper<>(sessionRepository)
                        .eq(SiteActivityRedBagSessionPO::getSiteCode, siteCode)
                        .eq(SiteActivityRedBagSessionPO::getStatus, RedBagSessionStatusEnum.PROGRESS.getStatus())
                        .one();
                if (ObjUtil.isNotEmpty(sessionPO)) {
                    return Lists.newArrayList(sessionPO.getSessionId());
                }
            } else {
                // key == today
                List<SiteActivityRedBagSessionPO> list = new LambdaQueryChainWrapper<>(sessionRepository)
                        .select(SiteActivityRedBagSessionPO::getSessionId)
                        .eq(SiteActivityRedBagSessionPO::getSiteCode, siteCode)
                        .eq(SiteActivityRedBagSessionPO::getDay, type)
                        .orderByAsc(SiteActivityRedBagSessionPO::getStartTime)
                        .list();
                if (ObjUtil.isNotEmpty(list)) {
                    return list.stream().map(SiteActivityRedBagSessionPO::getSessionId).collect(Collectors.toList());
                }
            }
        }
        return null;
    }

    @Override
    public Map<String, List<String>> queryDataList(List<String> keyList) {
        return Map.of();
    }
}
