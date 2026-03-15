package com.cloud.baowang.site.service;

import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.RiskTypeEnum;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.CodeValueResVO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDownReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelResVO;
import com.cloud.baowang.system.api.vo.risk.RiskTypeListResVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 通用服务方法
 */
@Service
@AllArgsConstructor
public class CommonService {

    private SystemParamApi systemParamApi;

    private RiskApi riskApi;

    public Map<String, List<CodeValueVO>> getSystemParamsByList(List<String> types) {
        ResponseVO<Map<String, List<CodeValueVO>>> systemParamsRO = systemParamApi.getSystemParamsByList(types);
        if (systemParamsRO.isOk()) {
            return systemParamsRO.getData();
        }
        return Maps.newHashMap();
    }

    public List<CodeValueResVO> getSystemParamsByListVO(List<String> types) {
        ResponseVO<List<CodeValueResVO>> systemParamsRO = systemParamApi.getSystemParamsByListVO(types);
        if (systemParamsRO.isOk()) {
            return systemParamsRO.getData();
        }
        return Lists.newArrayList();
    }

    public List<CodeValueNoI18VO> getRiskDownBox(RiskLevelDownReqVO vo) {
        // 风控类型 1:会员 2:代理 3:银行卡 4:虚拟币 5:IP 6:终端设备号
        ResponseVO<List<RiskLevelResVO>> riskControlLevelResponseVO = riskApi.getRiskLevelList(vo);

        List<CodeValueNoI18VO> riskControlLevel = Lists.newArrayList();
        if (riskControlLevelResponseVO.isOk()) {
            List<RiskLevelResVO> list = riskControlLevelResponseVO.getData();
            riskControlLevel = list.stream().map(item ->
                            CodeValueNoI18VO.builder()
                                    .code(item.getId())
                                    .value(item.getRiskControlLevel())
                                    .build())
                    .toList();
        }
        return riskControlLevel;
    }

    public List<RiskTypeListResVO> getRiskTypeDownBox() {
        ResponseVO<List<CodeValueVO>> responseVO = systemParamApi.getSystemParamByType(CommonConstant.RISK_CONTROL_TYPE);
        //添加label
        List<RiskTypeListResVO> list = new ArrayList<>();
        for (CodeValueVO systemParamVO : responseVO.getData()) {
            RiskTypeListResVO vo = new RiskTypeListResVO();
            BeanUtils.copyProperties(systemParamVO, vo);
            vo.setLabel(RiskTypeEnum.labelOfCode(systemParamVO.getCode()));
            list.add(vo);
        }
        return list;
    }

}
