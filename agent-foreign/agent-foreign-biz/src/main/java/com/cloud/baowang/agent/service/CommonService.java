package com.cloud.baowang.agent.service;

import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 通用服务方法
 */
@Service
@AllArgsConstructor
public class CommonService {

    private SystemParamApi systemParamApi;

    public Map<String, List<CodeValueVO>> getSystemParamsByList(List<String> types) {
        ResponseVO<Map<String, List<CodeValueVO>>> systemParamsRO = systemParamApi.getSystemParamsByList(types);
        if (systemParamsRO.isOk()) {
            return systemParamsRO.getData();
        }
        return Maps.newHashMap();
    }
}
