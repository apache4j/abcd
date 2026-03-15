package com.cloud.baowang.user.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.utils.LanguageUtils;
import com.cloud.baowang.user.api.vo.notice.user.usernoticeconfig.LanguageNoticeVO;
import com.cloud.baowang.user.po.LanguageNoticeConfigPO;
import com.cloud.baowang.user.repositories.LanguageNoticeConfigRepository;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: sheldon
 * @Date: 3/29/24 6:31 下午
 */
@Service
@AllArgsConstructor
public class LanguageNoticeConfigService extends ServiceImpl<LanguageNoticeConfigRepository, LanguageNoticeConfigPO> {

    private final LanguageNoticeConfigRepository languageNoticeConfigRepository;

    public void delLanguageName(String code, String paramId) {

        if (ObjectUtil.isEmpty(paramId)) {
            return;
        }

        if (ObjectUtil.isEmpty(paramId)) {
            return;
        }
        languageNoticeConfigRepository.delete(Wrappers.lambdaQuery(LanguageNoticeConfigPO.class)
                .eq(LanguageNoticeConfigPO::getCode, code)
                .eq(LanguageNoticeConfigPO::getParamId, paramId));
    }


    /**
     * 查指定业务的多语言
     *
     * @param code    业务code
     * @param paramId 上级ID
     */
    public Map<String, String> getLanguageMap(String code, List<String> paramId) {

        if (CollectionUtil.isEmpty(paramId) || ObjectUtil.isEmpty(code)) {
            return Maps.newHashMap();
        }

        List<LanguageNoticeConfigPO> languageNameConfigList = getLanguageList(code, LanguageUtils.getLanguage(), paramId);

        return languageNameConfigList.stream().collect(Collectors.toMap(LanguageNoticeConfigPO::getParamId, LanguageNoticeConfigPO::getName, (existingValue, newValue) -> newValue));
    }

    public List<LanguageNoticeConfigPO> getLanguageList(String code, String language, List<String> paramId) {
        paramId = paramId.stream().distinct().collect(Collectors.toList());
        LambdaQueryWrapper<LanguageNoticeConfigPO> wrapper = Wrappers.lambdaQuery(LanguageNoticeConfigPO.class)
                .eq(LanguageNoticeConfigPO::getCode, code)
                .in(LanguageNoticeConfigPO::getParamId, paramId);
        if (ObjectUtil.isNotEmpty(language)) {
            wrapper.eq(LanguageNoticeConfigPO::getLanguage, language);
        }
        return languageNoticeConfigRepository.selectList(wrapper);
    }


    /**
     * 查出语言并且转指定对象
     */
    public Map<String, List<LanguageNoticeVO>> getLanguageToNameVoMap(String code, String language, List<String> paramId) {
        Map<String, List<LanguageNoticeVO>> resultMap = Maps.newHashMap();
        if (CollectionUtil.isEmpty(paramId)) {
            return resultMap;
        }
        List<LanguageNoticeConfigPO> languageNameConfigList = getLanguageList(code, language, paramId);
        Map<String, List<LanguageNoticeConfigPO>> languageNameConfigMap = languageNameConfigList
                .stream()
                .collect(Collectors.groupingBy(LanguageNoticeConfigPO::getParamId));


        for (String id : paramId) {
            List<LanguageNoticeConfigPO> languageList = languageNameConfigMap.get(id);
            if (CollectionUtil.isNotEmpty(languageList)) {
                List<LanguageNoticeVO> languageNameList = languageList.stream().map(x -> LanguageNoticeVO.builder()
                        .name(x.getName())
                        .code(x.getLanguage())
                        .build()
                ).collect(Collectors.toList());
                resultMap.put(id, languageNameList);
            }
        }
        return resultMap;
    }


    /**
     * @param code    业务code
     * @param paramId 关联ID
     * @param list    语言集合
     */
    public void addLanguageNotice(String code, String paramId, List<LanguageNoticeVO> list) {

        if (CollectionUtil.isEmpty(list)) {
            return;
        }

        if (ObjectUtil.isEmpty(paramId)) {
            return;
        }

        delLanguageName(code, paramId);
        List<LanguageNoticeConfigPO> insertList = new ArrayList<>();
        for (LanguageNoticeVO item : list) {
            LanguageNoticeConfigPO po = LanguageNoticeConfigPO.builder()
                    .code(code)
                    .paramId(paramId)
                    .name(item.getName())
                    .language(item.getCode())
                    .build();
            po.setCreatedTime(System.currentTimeMillis());
            insertList.add(po);
            //languageNameConfigRepository.insert(po);

        }
        this.saveBatch(insertList);

    }


}
