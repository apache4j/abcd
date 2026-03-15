package com.cloud.baowang.system.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CacheConstants;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.CodeValueResVO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.system.po.SystemParamPO;
import com.cloud.baowang.system.repositories.SystemParamRepository;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SystemParamService extends ServiceImpl<SystemParamRepository,SystemParamPO> {

    @Autowired
    private SystemParamRepository systemParamRepository;

    public List<CodeValueVO> getSystemParamByType(String type){
        LambdaQueryWrapper<SystemParamPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SystemParamPO::getType, type);
        queryWrapper.orderByAsc(SystemParamPO::getCreator);
        List<SystemParamPO> list = list(queryWrapper);
        if (CollectionUtil.isEmpty(list)){
            return Lists.newArrayList();
        }
        return ConvertUtil.entityListToModelList(list, CodeValueVO.class);
    }


    @Cacheable(cacheNames = {CacheConstants.SYSTEM_BUSINESS_PARAM_CACHE}, key = "#list")
    public Map<String, List<CodeValueVO>> getSystemParamsByList(List<String> list) {
        LambdaQueryWrapper<SystemParamPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SystemParamPO::getType, list);
        List<SystemParamPO> queryResult = list(queryWrapper);
        if (CollectionUtil.isEmpty(queryResult)){
            return Maps.newHashMap();
        }
        List<CodeValueVO> result = ConvertUtil.entityListToModelList(queryResult, CodeValueVO.class);
        return result.stream().collect(Collectors.groupingBy(CodeValueVO::getType));

    }

    @Cacheable(cacheNames = {CacheConstants.SYSTEM_BUSINESS_PARAM_CACHE}, key = "#list.size()")
    public List<CodeValueResVO> getSystemParamsByListVO(List<String> list) {
        List<CodeValueResVO> resVO = Lists.newArrayList();
        LambdaQueryWrapper<SystemParamPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SystemParamPO::getType, list);
        List<SystemParamPO> queryResult = list(queryWrapper);
        if (CollectionUtil.isEmpty(queryResult)){
            return resVO;
        }

        List<CodeValueVO> result = ConvertUtil.entityListToModelList(queryResult, CodeValueVO.class);
        Map<String, List<CodeValueVO>> map =  result.stream().collect(Collectors.groupingBy(CodeValueVO::getType));
        for(Map.Entry<String,List<CodeValueVO>> entity: map.entrySet()){
            resVO.add(new CodeValueResVO(entity.getKey(),entity.getValue()));
        }
        return resVO;

    }

    private CodeValueVO trsnsformSystemParamVO(final SystemParamPO systemParamPO) {
        return CodeValueVO.builder()
                .code(systemParamPO.getCode())
                .value(systemParamPO.getValue())
                .build();
    }

    public Map<String, String> getSystemParamMap(final String str) {
        final List<CodeValueVO> systemParamVOList = getSystemParamByType(str);
        return systemParamVOList.stream()
                .collect(Collectors.toMap(CodeValueVO::getCode, CodeValueVO::getValue));
    }

    public Map<String, Map<String, String>> getSystemParamsMapByList(List<String> list) {
        List<SystemParamPO> queryResult = list(Wrappers.<SystemParamPO>lambdaQuery().in(SystemParamPO::getType, list));
        if (CollectionUtil.isEmpty(queryResult)){
            return Maps.newHashMap();
        }
        List<CodeValueVO> result = ConvertUtil.entityListToModelList(queryResult, CodeValueVO.class);
        return result.stream()
                .collect(Collectors.groupingBy(CodeValueVO::getType,
                        Collectors.toMap(CodeValueVO::getCode, CodeValueVO::getValue, (o, n) -> n)));
    }

    public List<CodeValueVO> getSystemParamByValue(List<String> list) {
        List<SystemParamPO> systemParamPOS = new LambdaQueryChainWrapper<>(baseMapper)
                .in(SystemParamPO::getValue, list)
                .list();
        if (CollUtil.isNotEmpty(systemParamPOS)) {
            return systemParamPOS.stream()
                    .map(this::trsnsformSystemParamVO).toList();
        }
        return null;
    }

    public SystemParamPO getSystemParamByTypeAndCode(String type,String code){
        LambdaQueryWrapper<SystemParamPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SystemParamPO::getType, type);
        queryWrapper.eq(SystemParamPO::getCode, code);
        List<SystemParamPO> list = list(queryWrapper);
        if (CollectionUtil.isEmpty(list)){
            return null;
        }
        return  list.get(0);
    }

}
