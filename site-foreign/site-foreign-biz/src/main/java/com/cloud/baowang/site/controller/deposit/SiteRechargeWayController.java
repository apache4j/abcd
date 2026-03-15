package com.cloud.baowang.site.controller.deposit;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.vo.CurrencyCodeReqVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.user.api.api.vip.SiteVipOptionApi;
import com.cloud.baowang.user.api.api.vip.VipGradeApi;
import com.cloud.baowang.user.api.vo.vip.VIPGradeVO;
import com.cloud.baowang.wallet.api.api.SiteRechargeWayApi;
import com.cloud.baowang.wallet.api.api.SystemRechargeTypeApi;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeChangeVipUseScopeVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeWayRequestVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeWayResponseVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeWayStatusReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeWayVipUseScopeVO;
import com.cloud.baowang.wallet.api.vo.recharge.SortNewReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeTypeRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeWayReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeWayRespVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Desciption:
 * @Author: qiqi
 **/
@RestController
@Tag(name = "资金-支付通道管理-存款方式管理")
@RequestMapping("/exchange/siteRechargeWay")
@AllArgsConstructor
public class SiteRechargeWayController {

    private final SiteRechargeWayApi siteRechargeWayApi;

    private final SystemRechargeTypeApi systemRechargeTypeApi;

    private final SystemParamApi systemParamApi;

    private final SiteVipOptionApi siteVipOptionApi;


    @PostMapping("selectRechargePage")
    @Operation(summary = "站点值方式分页查询")
    public ResponseVO<Page<SiteRechargeWayResponseVO>> selectRechargePage(@RequestBody @Validated SiteRechargeWayRequestVO siteRechargeWayRequestVO){
        siteRechargeWayRequestVO.setSiteCode(CurrReqUtils.getSiteCode());
        return siteRechargeWayApi.selectRechargePage(siteRechargeWayRequestVO);
    }

    @PostMapping("enableOrDisable")
    @Operation(summary = "站点充值方式启用禁用")
    public ResponseVO<Void> enableOrDisable(@RequestBody @Validated SiteRechargeWayStatusReqVO siteRechargeWayStatusReqVO){
        siteRechargeWayStatusReqVO.setOperatorUserNo(CurrReqUtils.getAccount());
        return siteRechargeWayApi.enableOrDisable(siteRechargeWayStatusReqVO);
    }
    @PostMapping("selectBySort")
    @Operation(summary = "充值方式排序查询")
    ResponseVO<List<SiteRechargeWayResponseVO>> selectBySort(@RequestBody @Validated SiteRechargeWayRequestVO siteRechargeWayRequestVO){
        siteRechargeWayRequestVO.setSiteCode(CurrReqUtils.getSiteCode());
        return siteRechargeWayApi.selectBySort(siteRechargeWayRequestVO);
    }
    @PostMapping("batchSaveSort")
    @Operation(summary = "批量保存充值方式排序")
    ResponseVO<Boolean> batchSaveSort(@RequestBody List<SortNewReqVO> sortNewReqVOS){
        return siteRechargeWayApi.batchSaveSort(CurrReqUtils.getAccount(),sortNewReqVOS);
    }

    @PostMapping("saveVipGradeUseScope")
    @Operation(summary = "vip等级使用范围")
    ResponseVO<Boolean> saveVipGradeUseScope(@RequestBody SiteRechargeWayVipUseScopeVO siteRechargeWayVipUseScopeVO){
        return siteRechargeWayApi.saveVipGradeUseScope(siteRechargeWayVipUseScopeVO);
    }
    @Operation(summary = "下拉框 ENABLE_DISABLE_TYPE 启用禁用状态")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox(@RequestBody @Validated CurrencyCodeReqVO currencyCodeReqVO) {

        List<String> param = new ArrayList<>();
        //状态
        param.add(CommonConstant.ENABLE_DISABLE_STATUS);
        //费率类型
        param.add(CommonConstant.FEE_TYPE);
        Map<String, List<CodeValueVO>> result = new HashMap<>();
        ResponseVO<Map<String, List<CodeValueVO>>> responseVO = systemParamApi.getSystemParamsByList(param);
        if(responseVO.isOk()){
            result = responseVO.getData();
        }
        List<CodeValueVO> rechargeTypeEnums = Lists.newArrayList();
        ResponseVO<List<SystemRechargeTypeRespVO>> listRechargeTypeResponseVO=systemRechargeTypeApi.selectAll();
        if(listRechargeTypeResponseVO.isOk()){
            List<SystemRechargeTypeRespVO> systemRechargeTypeRespVOS=listRechargeTypeResponseVO.getData();
            systemRechargeTypeRespVOS=systemRechargeTypeRespVOS.stream().filter(o->o.getCurrencyCode().equals(currencyCodeReqVO.getCurrencyCode())).collect(Collectors.toUnmodifiableList());
            for(SystemRechargeTypeRespVO systemRechargeTypeRespVO:systemRechargeTypeRespVOS){
                CodeValueVO codeValueVO=new CodeValueVO();
                codeValueVO.setType(systemRechargeTypeRespVO.getRechargeCode());
                codeValueVO.setCode(systemRechargeTypeRespVO.getId());
                codeValueVO.setValue(systemRechargeTypeRespVO.getRechargeTypeI18());
                rechargeTypeEnums.add(codeValueVO);
            }
        }
        result.put("rechargeTypeEnums", rechargeTypeEnums);

        ResponseVO<List<VIPGradeVO>> vipGradeResponseVO = siteVipOptionApi.getCnVipGradeList();
        if(responseVO.isOk()){
            List<VIPGradeVO> list = vipGradeResponseVO.getData();
            List<CodeValueVO> codeValueNoI18VOList = new ArrayList<>();
            for (VIPGradeVO vipGradeVO:list) {
                CodeValueVO codeValueVO = new CodeValueVO();
                codeValueVO.setCode(String.valueOf(vipGradeVO.getVipGradeCode()));
                codeValueVO.setValue(vipGradeVO.getVipGradeName());
                codeValueNoI18VOList.add(codeValueVO);
            }
            result.put("vipGrades", codeValueNoI18VOList);
        }
        return ResponseVO.success(result);
    }






}
