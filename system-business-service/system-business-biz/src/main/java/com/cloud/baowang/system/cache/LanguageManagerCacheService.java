package com.cloud.baowang.system.cache;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.cloud.baowang.common.core.constants.CacheConstants;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.system.api.vo.language.LanguageManagerListVO;
import com.cloud.baowang.system.po.lang.LanguageManagerPO;
import com.cloud.baowang.system.repositories.language.LanguageManagerRepository;
import com.github.jesse.l2cache.spring.biz.AbstractCacheService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
//@Component
@AllArgsConstructor
public class LanguageManagerCacheService extends AbstractCacheService<String, List<LanguageManagerListVO>> {
    private final LanguageManagerRepository languageManagerRepository;

    @Override
    public String getCacheName() {
        return CacheConstants.LANGUAGE_CACHE;
    }


    @Override
    public List<LanguageManagerListVO> queryData(String key) {
        String siteCode = CurrReqUtils.getSiteCode();
        List<LanguageManagerPO> list = new LambdaQueryChainWrapper<>(languageManagerRepository)
                .eq(StrUtil.isNotBlank(siteCode), LanguageManagerPO::getSiteCode, siteCode)
                .orderByAsc(LanguageManagerPO::getCreatedTime)
                .list();
        if (CollUtil.isEmpty(list)) {
            return List.of();
        }
        return ConvertUtil.entityListToModelList(list, LanguageManagerListVO.class);
    }

    @Override
    public Map<String, List<LanguageManagerListVO>> queryDataList(List<String> keyList) {
        List<LanguageManagerPO> list = new LambdaQueryChainWrapper<>(languageManagerRepository)
                .orderByAsc(LanguageManagerPO::getCreatedTime)
                .list();
        List<LanguageManagerListVO> languageManagerListVOS = ConvertUtil.entityListToModelList(list, LanguageManagerListVO.class);
        return languageManagerListVOS.stream().collect(Collectors.groupingBy(LanguageManagerListVO::getSiteCode));
    }

    /**
     * clear 重写 防止并发事务内清除缓存失效
     */
    @Override
    public void clear() {
        List<LanguageManagerPO> poList = new LambdaQueryChainWrapper<>(languageManagerRepository)
                .select(LanguageManagerPO::getSiteCode)
                .groupBy(LanguageManagerPO::getSiteCode)
                .list();
        List<String> keyList = poList.stream().map(LanguageManagerPO::getSiteCode).toList();
        super.batchReload(keyList);
    }
}
