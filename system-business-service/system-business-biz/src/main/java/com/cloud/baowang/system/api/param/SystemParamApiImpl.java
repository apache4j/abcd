package com.cloud.baowang.system.api.param;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.baowang.common.core.constants.CacheConstants;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.vo.base.CodeValueResVO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.po.SystemParamPO;
import com.cloud.baowang.system.service.SystemParamService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class SystemParamApiImpl implements SystemParamApi {

    private final SystemParamService systemParamService;


    @Override
    public ResponseVO<List<CodeValueVO>> getSystemParamByType(String type) {
        return ResponseVO.success(systemParamService.getSystemParamByType(type));
    }



    @Override
    public ResponseVO<Map<String, List<CodeValueVO>>> getSystemParamsByList(List<String> list) {
        if (CollectionUtil.isEmpty(list)){
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        return ResponseVO.success(systemParamService.getSystemParamsByList(list));
    }

    @Override
    public ResponseVO<List<CodeValueResVO>> getSystemParamsByListVO(List<String> list) {
        if (CollectionUtil.isEmpty(list)){
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        return ResponseVO.success(systemParamService.getSystemParamsByListVO(list));
    }


    @Override
    public ResponseVO<Map<String, String>> getSystemParamMap(String type) {
        final LambdaQueryWrapper<SystemParamPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SystemParamPO::getType, type);
        final List<SystemParamPO> systemParamPOList = systemParamService.list(queryWrapper);
        return ResponseVO.success(systemParamPOList.stream().collect(Collectors.toMap(SystemParamPO::getCode,
                SystemParamPO::getValue)));
    }

    @Override
    public ResponseVO<Map<String, Map<String, String>>> getSystemParamsMapByList(List<String> list) {
        return ResponseVO.success(systemParamService.getSystemParamsMapByList(list));
    }

    @Override
    public ResponseVO<List<CodeValueVO>> getSystemParamByTypeCaChe(String type) {
        String key = CacheConstants.SYSTEM_BUSINESS_PARAM_CACHE + ":" + type;
        List<CodeValueVO> ret = (List<CodeValueVO>) RedisUtil.getLocalCachedMap(CacheConstants.KEY_SYSTEM_PARAM, key);
        if (ret != null){
            return ResponseVO.success(ret);
        }
        List<CodeValueVO> systemParam = systemParamService.getSystemParamByType(type);
        RedisUtil.setLocalCachedMap(CacheConstants.KEY_SYSTEM_PARAM, key, systemParam);
        return ResponseVO.success(systemParam);
    }

    @Override
    public Map<String, String> getSystemParamMapInner(String type) {
        final LambdaQueryWrapper<SystemParamPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SystemParamPO::getType, type);
        final List<SystemParamPO> systemParamPOList = systemParamService.list(queryWrapper);
        return systemParamPOList.stream().collect(Collectors.toMap(SystemParamPO::getCode,
                SystemParamPO::getValue));
    }


}
