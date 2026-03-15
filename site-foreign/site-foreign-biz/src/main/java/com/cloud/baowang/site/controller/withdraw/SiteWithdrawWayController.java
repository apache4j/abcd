package com.cloud.baowang.site.controller.withdraw;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.vo.CurrencyCodeReqVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.wallet.api.api.SiteWithdrawWayApi;
import com.cloud.baowang.wallet.api.api.SystemWithdrawTypeApi;
import com.cloud.baowang.wallet.api.vo.recharge.SortNewReqVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawWayRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawWayResponseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawWayStatusRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawTypeResponseVO;
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
@Tag(name = "资金-支付通道管理-提款方式管理")
@RequestMapping("/exchange/siteWithdrawWay")
@AllArgsConstructor
public class SiteWithdrawWayController {

    private final SiteWithdrawWayApi siteWithdrawWayApi;

    private final SystemWithdrawTypeApi systemWithdrawTypeApi;

    private final SystemParamApi systemParamApi;


    @PostMapping("selectWithdrawPage")
    @Operation(summary = "站点值方式分页查询")
    public ResponseVO<Page<SiteWithdrawWayResponseVO>> selectWithdrawPage(@RequestBody @Validated SiteWithdrawWayRequestVO siteWithdrawWayRequestVO){
        siteWithdrawWayRequestVO.setSiteCode(CurrReqUtils.getSiteCode());
        return siteWithdrawWayApi.selectWithdrawPage(siteWithdrawWayRequestVO);
    }

    @PostMapping("enableOrDisable")
    @Operation(summary = "站点充值方式启用禁用")
    public ResponseVO<Void> enableOrDisable(@RequestBody @Validated SiteWithdrawWayStatusRequestVO siteWithdrawWayStatusReqVO){
        siteWithdrawWayStatusReqVO.setOperatorUserNo(CurrReqUtils.getAccount());
        return siteWithdrawWayApi.enableOrDisable(siteWithdrawWayStatusReqVO);
    }
    @PostMapping("selectBySort")
    @Operation(summary = "充值方式排序查询")
    ResponseVO<List<SiteWithdrawWayResponseVO>> selectBySort(@RequestBody @Validated SiteWithdrawWayRequestVO siteWithdrawWayRequestVO){
        siteWithdrawWayRequestVO.setSiteCode(CurrReqUtils.getSiteCode());
        return siteWithdrawWayApi.selectBySort(siteWithdrawWayRequestVO);
    }
    @PostMapping("batchSaveSort")
    @Operation(summary = "批量保存充值方式排序")
    ResponseVO<Boolean> batchSaveSort(@RequestBody List<SortNewReqVO> sortNewReqVOS){
        return siteWithdrawWayApi.batchSaveSort(CurrReqUtils.getAccount(),sortNewReqVOS);
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
        List<CodeValueVO> withdrawTypeEnums = Lists.newArrayList();
        ResponseVO<List<SystemWithdrawTypeResponseVO>> listWithdrawTypeResponseVO=systemWithdrawTypeApi.selectAll();
        if(listWithdrawTypeResponseVO.isOk()){
            List<SystemWithdrawTypeResponseVO> systemWithdrawTypeRespVOS=listWithdrawTypeResponseVO.getData();
            systemWithdrawTypeRespVOS=systemWithdrawTypeRespVOS.stream().filter(o->o.getCurrencyCode().equals(currencyCodeReqVO.getCurrencyCode())).collect(Collectors.toUnmodifiableList());
            for(SystemWithdrawTypeResponseVO systemWithdrawTypeRespVO:systemWithdrawTypeRespVOS){
                CodeValueVO codeValueVO=new CodeValueVO();
                codeValueVO.setType(systemWithdrawTypeRespVO.getWithdrawTypeCode());
                codeValueVO.setCode(systemWithdrawTypeRespVO.getId());
                codeValueVO.setValue(systemWithdrawTypeRespVO.getWithdrawTypeI18());
                withdrawTypeEnums.add(codeValueVO);
            }
        }
        result.put("withdrawTypeEnums", withdrawTypeEnums);
        return ResponseVO.success(result);
    }






}
