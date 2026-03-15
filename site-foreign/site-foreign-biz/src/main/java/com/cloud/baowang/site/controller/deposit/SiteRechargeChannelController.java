package com.cloud.baowang.site.controller.deposit;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.vo.CurrencyCodeReqVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.AddressUtils;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.user.api.api.vip.SiteVipOptionApi;
import com.cloud.baowang.user.api.api.vip.VipGradeApi;
import com.cloud.baowang.user.api.vo.vip.VIPGradeVO;
import com.cloud.baowang.wallet.api.api.SiteRechargeChannelApi;
import com.cloud.baowang.wallet.api.api.SiteRechargeWayApi;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeChangeVipUseScopeVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeChannelAddressReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeChannelRecvInfoVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeChannelReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeChannelRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeChannelStatusReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeWayRequestVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeWayResponseVO;
import com.cloud.baowang.wallet.api.vo.recharge.SortNewReqVO;
import com.google.common.collect.Lists;
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
@Tag(name = "资金-支付通道管理-存款通道管理")
@RequestMapping("/exchange/siteRechargeChannel")
@AllArgsConstructor
public class SiteRechargeChannelController {

    private final SiteRechargeChannelApi siteRechargeChannelApi;

    private final SiteRechargeWayApi siteRechargeWayApi;

    private final SystemParamApi systemParamApi;

    private final SiteVipOptionApi siteVipOptionApi;



    @PostMapping("selectRechargePage")
    @Operation(summary = "站点充值通道分页查询")
    ResponseVO<Page<SiteRechargeChannelRespVO>> selectRechargePage(@RequestBody @Validated SiteRechargeChannelReqVO siteRechargeChannelReqVO){
        siteRechargeChannelReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        return siteRechargeChannelApi.selectRechargePage(siteRechargeChannelReqVO);
    }

    @PostMapping("selectBySort")
    @Operation(summary = "充值通道排序查询")
    ResponseVO<List<SiteRechargeChannelRespVO>> selectBySort(@RequestBody @Validated SiteRechargeChannelReqVO siteRechargeChannelReqVO){
        siteRechargeChannelReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        return siteRechargeChannelApi.selectBySort(siteRechargeChannelReqVO);
    }
    @PostMapping("batchSaveSort")
    @Operation(summary = "批量保存充值通道排序")
    ResponseVO<Boolean> batchSaveSort(@RequestBody List<SortNewReqVO> sortNewReqVOS){
        return siteRechargeChannelApi.batchSaveSort(CurrReqUtils.getAccount(),sortNewReqVOS);
    }

    @PostMapping("saveReceiveInfo")
    @Operation(summary = "收款信息保存")
    ResponseVO<Boolean> saveReceiveInfo(@RequestBody SiteRechargeChannelRecvInfoVO siteRechargeChannelRecvInfoVO){
        siteRechargeChannelRecvInfoVO.setCurrentUserNo(CurrReqUtils.getAccount());
        return siteRechargeChannelApi.saveReceiveInfo(siteRechargeChannelRecvInfoVO);
    }

    @PostMapping("saveVipGradeUseScope")
    @Operation(summary = "vip等级使用范围")
    ResponseVO<Boolean> saveVipGradeUseScope(@RequestBody SiteRechargeChangeVipUseScopeVO siteRechargeChangeVipUseScopeVO){
        return siteRechargeChannelApi.saveVipGradeUseScope(siteRechargeChangeVipUseScopeVO);
    }

    @PostMapping("enableOrDisable")
    @Operation(summary = "充值通道启用禁用")
    ResponseVO<Void> enableOrDisable(@RequestBody @Validated SiteRechargeChannelStatusReqVO siteRechargeChannelStatusReqVO){
        siteRechargeChannelStatusReqVO.setOperatorUserNo(CurrReqUtils.getAccount());
        return siteRechargeChannelApi.enableOrDisable(siteRechargeChannelStatusReqVO);
    }

    @PostMapping("validAddress")
    @Operation(summary = "虚拟币地址合法性校验")
    ResponseVO<Void> validAddress(@RequestBody @Validated SiteRechargeChannelAddressReqVO vo){
       boolean addressValidFlag = AddressUtils.isValidAddress(vo.getAddressNo(), vo.getNetWorkTypeEnum().getType());
       if(addressValidFlag){
           return ResponseVO.success();
       }else {
           return ResponseVO.fail(ResultCode.WITHDRAW_ADDRESS_ERROR,"地址不合法");
       }
    }


    @Operation(summary = "下拉框 类型从公共下拉框获取: CHANNEL_TYPE ENABLE_DISABLE_TYPE 启用禁用状态")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox(@RequestBody @Validated CurrencyCodeReqVO currencyCodeReqVO) {

        List<String> param = new ArrayList<>();
        //状态
        param.add(CommonConstant.ENABLE_DISABLE_STATUS);
        //通道类型
        param.add(CommonConstant.CHANNEL_TYPE);
        Map<String, List<CodeValueVO>> result = new HashMap<>();
        ResponseVO<Map<String, List<CodeValueVO>>> responseVO = systemParamApi.getSystemParamsByList(param);
        if(responseVO.isOk()){
            result = responseVO.getData();
        }
        List<CodeValueVO> rechargeWayEnums = Lists.newArrayList();
        SiteRechargeWayRequestVO vo = new SiteRechargeWayRequestVO();
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setCurrencyCode(currencyCodeReqVO.getCurrencyCode());
        ResponseVO<List<SiteRechargeWayResponseVO>> listResponseVO=siteRechargeWayApi.selectBySort(vo);
        if(listResponseVO.isOk()){
            List<SiteRechargeWayResponseVO> systemRechargeWayRespVOS=listResponseVO.getData();
            systemRechargeWayRespVOS=systemRechargeWayRespVOS.stream().filter(o->o.getCurrencyCode().equals(currencyCodeReqVO.getCurrencyCode())).collect(Collectors.toUnmodifiableList());
            for(SiteRechargeWayResponseVO systemRechargeWayRespVO:systemRechargeWayRespVOS){
                CodeValueVO codeValueVO=new CodeValueVO();
                codeValueVO.setType(systemRechargeWayRespVO.getCurrencyCode());
                codeValueVO.setCode(systemRechargeWayRespVO.getRechargeWayId());
                codeValueVO.setValue(systemRechargeWayRespVO.getRechargeWayI18());
                rechargeWayEnums.add(codeValueVO);
            }
        }

        result.put("rechargeWayEnums", rechargeWayEnums);
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
