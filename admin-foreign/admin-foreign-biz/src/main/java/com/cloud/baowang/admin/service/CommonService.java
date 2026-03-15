package com.cloud.baowang.admin.service;

import com.cloud.baowang.admin.controller.language.LanguageManagerController;
import com.cloud.baowang.agent.api.api.AgentLabelApi;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.CurrencyEnum;
import com.cloud.baowang.common.core.enums.RiskTypeEnum;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.CodeValueResVO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.api.SystemConfigApi;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.api.language.LanguageManagerApi;
import com.cloud.baowang.system.api.api.operations.SkinInfoApi;
import com.cloud.baowang.system.api.vo.language.LanguageManagerListVO;
import com.cloud.baowang.system.api.vo.operations.SkinResVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDownReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelResVO;
import com.cloud.baowang.system.api.vo.risk.RiskTypeListResVO;
import com.cloud.baowang.user.api.api.vip.VipRankApi;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 通用服务方法
 */
@Service
@AllArgsConstructor
public class CommonService {
    private final LanguageManagerController languageManagerController;
    private SystemParamApi systemParamApi;
    private SystemConfigApi systemConfigApi;
    private RiskApi riskApi;
    private VipRankApi vipRankApi;
    private AgentLabelApi agentLabelApi;
    private final SkinInfoApi skinInfoApi;
    private final LanguageManagerApi languageManagerApi;

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

    public ResponseVO<List<CodeValueVO>> getMainCurrency() {
        return systemConfigApi.getMainCurrency();
    }

    public List<CodeValueNoI18VO> getSkinDownBox() {
        ResponseVO<List<SkinResVO>> responseVO = skinInfoApi.querySkinList();
        List<CodeValueNoI18VO> skinList = Lists.newArrayList();
        if (responseVO.isOk()) {
            List<SkinResVO> list = responseVO.getData();
            skinList = list.stream().map(item ->
                    CodeValueNoI18VO.builder()
                                    .code(item.getSkinCode())
                                    .value(item.getSkinName())
                                    .build()).toList();
        }
        return skinList;
    }

    /**
     * 全球货币代码 符号 走枚举 固定
     *  // Set<Currency> currencySet = Currency.getAvailableCurrencies();
     * @return
     */
    public ResponseVO<List<CodeValueNoI18VO>> getEarthCurrencyList() {
        List<CodeValueNoI18VO> codeValueVOS=Lists.newArrayList();
        for(CurrencyEnum currencyEnum: CurrencyEnum.values()){
            CodeValueNoI18VO codeValueVO=new CodeValueNoI18VO();
            codeValueVO.setCode(currencyEnum.getCode());
            codeValueVO.setValue(currencyEnum.getName());
            codeValueVO.setType(currencyEnum.getSymbol());
            codeValueVOS.add(codeValueVO);
        }
        return ResponseVO.success(codeValueVOS);
    }

    public List<LanguageManagerListVO> getLangDownBox() {
        return languageManagerApi.list().getData();
    }

}
