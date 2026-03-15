package com.cloud.baowang.common.data.transfer.i18n.cache;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import com.cloud.baowang.common.core.constants.CacheConstants;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.api.i18n.dto.I18NMessageDTO;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.N;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * i18n缓存
 */
@Slf4j
@Component
public class I18nMessageCache {

    /**
     * i18n redis缓存过期时间 默认一天刷一次
     */
    private static final Long I18N_MESSAGE_EXPIRE = 60 * 60 * 24L;
    private static final Map<String, String> emptyMap = Maps.newHashMap();
    @Autowired
    private I18nApi i18nApi;


    /**
     * 根据i18n key查询,先查本地缓存 key:messageKey
     *
     * @param messageKey message键
     * @return map<language, messageContent>
     */
    public Map<String, String> getDBI18nMessageByKey(String messageKey) {
        Map<String, String> value = (Map<String, String>) RedisUtil.getLocalCachedMap(CacheConstants.KEY_I18N_MESSAGE, messageKey);
        if (ObjUtil.isNotNull(value)) {
            return value;
        }
        Map<String, String> map = Maps.newHashMap();
        ResponseVO<List<I18NMessageDTO>> responseVO = i18nApi.getMessageByKey(messageKey);
        if (responseVO.isOk()) {
            List<I18NMessageDTO> data = responseVO.getData();
            if (CollUtil.isNotEmpty(data)) {
                map = data.stream().collect(
                        Collectors.toMap(I18NMessageDTO::getLanguage, I18NMessageDTO::getMessage, (k1, k2) -> k2));
                // 放入redis缓存
                RedisUtil.setLocalCachedMap(CacheConstants.KEY_I18N_MESSAGE, messageKey, map);
            } else {
                log.warn("查询i18n为空,messageKey:{},site:{},oneId:{},acount:{}", messageKey, CurrReqUtils.getSiteCode(), CurrReqUtils.getOneId(), CurrReqUtils.getAccount());
                RedisUtil.setLocalCachedMap(CacheConstants.KEY_I18N_MESSAGE, messageKey, emptyMap);
            }
        }
        return map;
    }

}
