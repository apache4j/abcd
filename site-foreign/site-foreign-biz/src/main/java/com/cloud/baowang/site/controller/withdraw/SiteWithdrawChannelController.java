package com.cloud.baowang.site.controller.withdraw;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.vo.CurrencyCodeReqVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.wallet.api.api.SiteWithdrawChannelApi;
import com.cloud.baowang.wallet.api.api.SiteWithdrawWayApi;
import com.cloud.baowang.wallet.api.vo.recharge.SortNewReqVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawChannelRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawChannelResponseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawChannelStatusRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawWayRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawWayResponseVO;
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
@Tag(name = "资金-支付通道管理-提款通道管理")
@RequestMapping("/exchange/siteWithdrawChannel")
@AllArgsConstructor
public class SiteWithdrawChannelController {

    private final SiteWithdrawChannelApi siteWithdrawChannelApi;

    private final SiteWithdrawWayApi siteWithdrawWayApi;

    private final SystemParamApi systemParamApi;



    @PostMapping("selectWithdrawPage")
    @Operation(summary = "站点提款通道分页查询")
    ResponseVO<Page<SiteWithdrawChannelResponseVO>> selectWithdrawPage(@RequestBody @Validated SiteWithdrawChannelRequestVO siteWithdrawChannelReqVO){
        siteWithdrawChannelReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        return siteWithdrawChannelApi.selectWithdrawPage(siteWithdrawChannelReqVO);
    }

    @PostMapping("selectBySort")
    @Operation(summary = "提款通道排序查询")
    ResponseVO<List<SiteWithdrawChannelResponseVO>> selectBySort(@RequestBody @Validated SiteWithdrawChannelRequestVO siteWithdrawChannelReqVO){
        siteWithdrawChannelReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        return siteWithdrawChannelApi.selectBySort(siteWithdrawChannelReqVO);
    }
    @PostMapping("batchSaveSort")
    @Operation(summary = "批量保存提款通道排序")
    ResponseVO<Boolean> batchSaveSort(@RequestBody List<SortNewReqVO> sortNewReqVOS){
        return siteWithdrawChannelApi.batchSaveSort(CurrReqUtils.getAccount(),sortNewReqVOS);
    }

    @PostMapping("enableOrDisable")
    @Operation(summary = "提款通道启用禁用")
    ResponseVO<Void> enableOrDisable(@RequestBody @Validated SiteWithdrawChannelStatusRequestVO siteWithdrawChannelStatusReqVO){
        siteWithdrawChannelStatusReqVO.setOperatorUserNo(CurrReqUtils.getAccount());
        return siteWithdrawChannelApi.enableOrDisable(siteWithdrawChannelStatusReqVO);
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
        List<CodeValueVO> withdrawWayEnums = Lists.newArrayList();
        SiteWithdrawWayRequestVO vo = new SiteWithdrawWayRequestVO();
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setCurrencyCode(currencyCodeReqVO.getCurrencyCode());
        ResponseVO<List<SiteWithdrawWayResponseVO>> listResponseVO=siteWithdrawWayApi.selectBySort(vo);
        if(listResponseVO.isOk()){
            List<SiteWithdrawWayResponseVO> systemWithdrawWayRespVOS=listResponseVO.getData();
            systemWithdrawWayRespVOS=systemWithdrawWayRespVOS.stream().filter(o->o.getCurrencyCode().equals(currencyCodeReqVO.getCurrencyCode())).collect(Collectors.toUnmodifiableList());
            for(SiteWithdrawWayResponseVO systemWithdrawWayRespVO:systemWithdrawWayRespVOS){
                CodeValueVO codeValueVO=new CodeValueVO();
                codeValueVO.setType(systemWithdrawWayRespVO.getCurrencyCode());
                codeValueVO.setCode(systemWithdrawWayRespVO.getWithdrawWayId());
                codeValueVO.setValue(systemWithdrawWayRespVO.getWithdrawWayI18());
                withdrawWayEnums.add(codeValueVO);
            }
        }

        result.put("withdrawWayEnums", withdrawWayEnums);

        return ResponseVO.success(result);
    }



}
