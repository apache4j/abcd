package com.cloud.baowang.admin.controller;

import com.cloud.baowang.admin.service.CommonService;
import com.cloud.baowang.admin.vo.CurrencyCodeReqVO;
import com.cloud.baowang.admin.vo.SiteCodeReqVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.NetWorkTypeEnum;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.CodeValueResVO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.vo.language.LanguageManagerListVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDownReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskTypeListResVO;
import com.cloud.baowang.wallet.api.api.*;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyInfoRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeTypeRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeWayRespVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawChannelResponseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawTypeResponseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawWayResponseVO;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "公共controller")
@RestController
@RequestMapping("/common")
@AllArgsConstructor
public class CommonController {

    private final CommonService commonService;

    private final SystemRechargeWayApi systemRechargeWayApi;

    private final SystemRechargeTypeApi systemRechargeTypeApi;

    private final SystemRechargeChannelApi systemRechargeChannelApi;


    private final SystemWithdrawWayApi systemWithdrawWayApi;

    private final SystemWithdrawTypeApi systemWithdrawTypeApi;

    private final SystemWithdrawChannelApi systemWithdrawChannelApi;

    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    @Operation(summary = "下拉框-通用")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox(@RequestBody List<String> types) {
        return ResponseVO.success(commonService.getSystemParamsByList(types));
    }

    @Operation(summary = "下拉框-通用-选择站点")
    @PostMapping(value = "/getDownBoxWithSiteCode/{siteCode}")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBoxWithSiteCode(@PathVariable("siteCode") String siteCode,
                                                                             @RequestBody List<String> types) {
        return ResponseVO.success(commonService.getSystemParamsByList(types));
    }

    @Operation(summary = "下拉框-通用")
    @PostMapping(value = "/getDownBoxDemo")
    public ResponseVO<List<CodeValueResVO>> getDownBoxDemo(@RequestBody List<String> types) {
        return ResponseVO.success(commonService.getSystemParamsByListVO(types));
    }


    @Operation(summary = "下拉框-风控层级")
    @PostMapping(value = "/getRiskDownBox")
    public ResponseVO<List<CodeValueNoI18VO>> getRiskDownBox(@Valid @RequestBody RiskLevelDownReqVO vo) {
        return ResponseVO.success(commonService.getRiskDownBox(vo));
    }

    @Operation(summary = "下拉框-风控类型")
    @PostMapping("getRiskTypeDownBox")
    public ResponseVO<List<RiskTypeListResVO>> getRiskTypeDownBox() {
        return ResponseVO.success(commonService.getRiskTypeDownBox());
    }

    @Operation(summary = "下拉框-主货币")
    @PostMapping(value = "/getMainCurrency")
    public ResponseVO<List<CodeValueVO>> getMainCurrency() {
        return commonService.getMainCurrency();
    }


    @Operation(summary = "下拉框-全球所有货币")
    @PostMapping(value = "/getEarthCurrencyList")
    public ResponseVO<List<CodeValueNoI18VO>> getEarthCurrencyList() {
        return commonService.getEarthCurrencyList();
    }


    @Operation(summary = "下拉框-皮肤")
    @PostMapping(value = "/getSkinDownBox")
    public ResponseVO<List<CodeValueNoI18VO>> getSkinDownBox() {
        return ResponseVO.success(commonService.getSkinDownBox());
    }

    @Operation(summary = "下拉框-语言")
    @PostMapping(value = "/getLangDownBox")
    public ResponseVO<List<LanguageManagerListVO>> getLangDownBox() {
        return ResponseVO.success(commonService.getLangDownBox());
    }

    @Operation(summary = "新增-修改下拉框 虚拟币网络类型")
    @PostMapping(value = "/getRechargeNetworks")
    public ResponseVO<List<CodeValueNoI18VO>> getWithDrawNetworks() {
        List<CodeValueNoI18VO> codeValueNoI18VOS = Lists.newArrayList();
        for (NetWorkTypeEnum NetWorkTypeEnum : NetWorkTypeEnum.values()) {
            CodeValueNoI18VO codeValueNoI18VO = new CodeValueNoI18VO();
            codeValueNoI18VO.setType("network");
            codeValueNoI18VO.setCode(NetWorkTypeEnum.getCode());
            codeValueNoI18VO.setValue(CommonConstant.USDT.concat("-").concat(NetWorkTypeEnum.getName()));
            codeValueNoI18VOS.add(codeValueNoI18VO);
        }
        return ResponseVO.success(codeValueNoI18VOS);
    }


    @Operation(summary = "充值类型下拉框")
    @PostMapping(value = "/rechargeTypeDownBox")
    public ResponseVO<List<CodeValueVO>> rechargeTypeDownBox(@RequestBody @Validated CurrencyCodeReqVO currencyCodeReqVO) {
        List<CodeValueVO> rechargeTypeEnums = Lists.newArrayList();
        ResponseVO<List<SystemRechargeTypeRespVO>> listRechargeTypeResponseVO = systemRechargeTypeApi.selectAll();
        if (listRechargeTypeResponseVO.isOk()) {
            List<SystemRechargeTypeRespVO> systemRechargeTypeRespVOS = listRechargeTypeResponseVO.getData();
            systemRechargeTypeRespVOS = systemRechargeTypeRespVOS.stream().filter(o -> o.getCurrencyCode().equals(currencyCodeReqVO.getCurrencyCode())).collect(Collectors.toUnmodifiableList());
            for (SystemRechargeTypeRespVO systemRechargeTypeRespVO : systemRechargeTypeRespVOS) {
                CodeValueVO codeValueVO = new CodeValueVO();
                codeValueVO.setType(systemRechargeTypeRespVO.getRechargeCode());
                codeValueVO.setCode(systemRechargeTypeRespVO.getId());
                codeValueVO.setValue(systemRechargeTypeRespVO.getRechargeTypeI18());
                rechargeTypeEnums.add(codeValueVO);
            }
        }
        return ResponseVO.success(rechargeTypeEnums);
    }

    @Operation(summary = "充值方式下拉框")
    @PostMapping(value = "/rechargeWayDownBox")
    public ResponseVO<List<CodeValueVO>> rechargeWayDownBox(@RequestBody @Validated CurrencyCodeReqVO currencyCodeReqVO) {
        List<CodeValueVO> rechargeWayEnums = Lists.newArrayList();
        ResponseVO<List<SystemRechargeWayRespVO>> listResponseVO = systemRechargeWayApi.selectAll();
        if (listResponseVO.isOk()) {
            List<SystemRechargeWayRespVO> systemRechargeWayRespVOS = listResponseVO.getData();
            systemRechargeWayRespVOS = systemRechargeWayRespVOS.stream().filter(o -> o.getCurrencyCode().equals(currencyCodeReqVO.getCurrencyCode())).collect(Collectors.toUnmodifiableList());
            for (SystemRechargeWayRespVO systemRechargeWayRespVO : systemRechargeWayRespVOS) {
                CodeValueVO codeValueVO = new CodeValueVO();
                codeValueVO.setType(systemRechargeWayRespVO.getRechargeTypeCode());
                codeValueVO.setCode(systemRechargeWayRespVO.getId());
                codeValueVO.setValue(systemRechargeWayRespVO.getRechargeWayI18());
                rechargeWayEnums.add(codeValueVO);
            }
        }
        return ResponseVO.success(rechargeWayEnums);
    }

    @Operation(summary = "充值通道下拉框")
    @PostMapping(value = "/rechargeChannelDownBox")
    public ResponseVO<List<CodeValueVO>> rechargeChannelDownBox(@RequestBody @Validated CurrencyCodeReqVO currencyCodeReqVO) {
        List<CodeValueVO> rechargeChannelEnums = Lists.newArrayList();
        ResponseVO<List<SystemRechargeChannelRespVO>> listResponseVO = systemRechargeChannelApi.selectAll();
        if (listResponseVO.isOk()) {
            List<SystemRechargeChannelRespVO> systemRechargeChannelRespVOS = listResponseVO.getData();
            systemRechargeChannelRespVOS = systemRechargeChannelRespVOS.stream().filter(o -> o.getCurrencyCode().equals(currencyCodeReqVO.getCurrencyCode())).collect(Collectors.toUnmodifiableList());
            for (SystemRechargeChannelRespVO systemRechargeChannelRespVO : systemRechargeChannelRespVOS) {
                CodeValueVO codeValueVO = new CodeValueVO();
                codeValueVO.setType(systemRechargeChannelRespVO.getChannelType());
                codeValueVO.setCode(systemRechargeChannelRespVO.getId());
                codeValueVO.setValue(systemRechargeChannelRespVO.getChannelName());
                rechargeChannelEnums.add(codeValueVO);
            }
        }
        return ResponseVO.success(rechargeChannelEnums);
    }


    @Operation(summary = "提现类型下拉框")
    @PostMapping(value = "/withDrawTypeDownBox")
    public ResponseVO<List<CodeValueVO>> withDrawTypeDownBox(@RequestBody @Validated CurrencyCodeReqVO currencyCodeReqVO) {

        List<CodeValueVO> withdrawTypeEnums = Lists.newArrayList();
        ResponseVO<List<SystemWithdrawTypeResponseVO>> listWithdrawTypeResponseVO = systemWithdrawTypeApi.selectAll();
        if (listWithdrawTypeResponseVO.isOk()) {
            List<SystemWithdrawTypeResponseVO> systemWithdrawTypeRespVOS = listWithdrawTypeResponseVO.getData();
            systemWithdrawTypeRespVOS = systemWithdrawTypeRespVOS.stream().filter(o -> o.getCurrencyCode().equals(currencyCodeReqVO.getCurrencyCode())).collect(Collectors.toUnmodifiableList());
            for (SystemWithdrawTypeResponseVO systemWithdrawTypeRespVO : systemWithdrawTypeRespVOS) {
                CodeValueVO codeValueVO = new CodeValueVO();
                codeValueVO.setType(systemWithdrawTypeRespVO.getWithdrawTypeCode());
                codeValueVO.setCode(systemWithdrawTypeRespVO.getId());
                codeValueVO.setValue(systemWithdrawTypeRespVO.getWithdrawTypeI18());
                withdrawTypeEnums.add(codeValueVO);
            }
        }
        return ResponseVO.success(withdrawTypeEnums);
    }

    @Operation(summary = "提现方式下拉框")
    @PostMapping(value = "/withDrawWayDownBox")
    public ResponseVO<List<CodeValueVO>> withDrawWayDownBox(@RequestBody @Validated CurrencyCodeReqVO currencyCodeReqVO) {
        List<CodeValueVO> withdrawWayEnums = Lists.newArrayList();
        ResponseVO<List<SystemWithdrawWayResponseVO>> listResponseVO = systemWithdrawWayApi.selectAll();
        if (listResponseVO.isOk()) {
            List<SystemWithdrawWayResponseVO> systemWithdrawWayResponseVOS = listResponseVO.getData();
            systemWithdrawWayResponseVOS = systemWithdrawWayResponseVOS.stream().filter(o -> o.getCurrencyCode().equals(currencyCodeReqVO.getCurrencyCode())).collect(Collectors.toUnmodifiableList());
            for (SystemWithdrawWayResponseVO systemWithdrawWayResponseVO : systemWithdrawWayResponseVOS) {
                CodeValueVO codeValueVO = new CodeValueVO();
                codeValueVO.setType(systemWithdrawWayResponseVO.getCurrencyCode());
                codeValueVO.setCode(systemWithdrawWayResponseVO.getId());
                codeValueVO.setValue(systemWithdrawWayResponseVO.getWithdrawWayI18());
                withdrawWayEnums.add(codeValueVO);
            }
        }
        return ResponseVO.success(withdrawWayEnums);
    }


    @Operation(summary = "提现通道下拉框")
    @PostMapping(value = "/withDrawChannelDownBox")
    public ResponseVO<List<CodeValueVO>> withDrawChannelDownBox(@RequestBody @Validated CurrencyCodeReqVO currencyCodeReqVO) {
        List<CodeValueVO> withDrawChannelEnums = Lists.newArrayList();
        ResponseVO<List<SystemWithdrawChannelResponseVO>    > listResponseVO = systemWithdrawChannelApi.selectAll();
        if (listResponseVO.isOk()) {
            List<SystemWithdrawChannelResponseVO> systemWithdrawChannelResponseVOS = listResponseVO.getData();
            systemWithdrawChannelResponseVOS = systemWithdrawChannelResponseVOS.stream().filter(o -> o.getCurrencyCode().equals(currencyCodeReqVO.getCurrencyCode())).collect(Collectors.toUnmodifiableList());
            for (SystemWithdrawChannelResponseVO systemWithdrawChannelResponseVO : systemWithdrawChannelResponseVOS) {
                CodeValueVO codeValueVO = new CodeValueVO();
                codeValueVO.setType(systemWithdrawChannelResponseVO.getChannelType());
                codeValueVO.setCode(systemWithdrawChannelResponseVO.getId());
                codeValueVO.setValue(systemWithdrawChannelResponseVO.getChannelName());
                withDrawChannelEnums.add(codeValueVO);
            }
        }
        return ResponseVO.success(withDrawChannelEnums);
    }

    @Operation(summary = "获取指定站点全部币种信息")
    @PostMapping(value = "/getCurrencyList")
    public ResponseVO<List<SiteCurrencyInfoRespVO>> getCurrencyList(@RequestBody @Validated SiteCodeReqVO vo) {
        return ResponseVO.success(siteCurrencyInfoApi.getBySiteCode(vo.getSiteCode()));
    }

}
