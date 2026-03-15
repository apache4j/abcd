package com.cloud.baowang.common.data.transfer.i18n.cache;

import cn.hutool.core.collection.CollUtil;
import com.cloud.baowang.common.core.constants.CacheConstants;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.language.LanguageManagerApi;
import com.cloud.baowang.system.api.vo.language.LanguageManagerListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LanguageInfoCache {

    @Autowired
    private LanguageManagerApi languageManagerApi;

    public LanguageManagerListVO getLanguageInfo(String langCode) {
        String siteCode = CurrReqUtils.getSiteCode();
        List<LanguageManagerListVO> value = (List<LanguageManagerListVO>) RedisUtil.getLocalCachedMap(CacheConstants.LANGUAGE_INFO, siteCode);
        if (CollUtil.isEmpty(value)) {
            value = languageManagerApi.list().getData();
        }
        for (LanguageManagerListVO s : value) {
            if (s.getCode().equals(langCode)) {
                return s;
            }
        }
        return null;
    }

    public List<LanguageManagerListVO> getAllLanguageInfo() {
        String siteCode = CurrReqUtils.getSiteCode();
        List<LanguageManagerListVO> languageManagerListVOS = (List<LanguageManagerListVO>) RedisUtil.getLocalCachedMap(CacheConstants.LANGUAGE_INFO, siteCode);
        if (CollUtil.isEmpty(languageManagerListVOS)) {
            languageManagerListVOS = languageManagerApi.list().getData();
        }
        return languageManagerListVOS;
    }
}
