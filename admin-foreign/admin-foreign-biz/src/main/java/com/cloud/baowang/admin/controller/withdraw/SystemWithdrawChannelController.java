package com.cloud.baowang.admin.controller.withdraw;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.admin.vo.CurrencyCodeReqVO;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.vip.VipRankApi;
import com.cloud.baowang.wallet.api.api.SystemWithdrawChannelApi;
import com.cloud.baowang.wallet.api.api.SystemWithdrawTypeApi;
import com.cloud.baowang.wallet.api.api.SystemWithdrawWayApi;
import com.cloud.baowang.wallet.api.vo.recharge.SortNewReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelRespVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawChannelAddVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawChannelRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawChannelResponseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawChannelStatusVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawChannelUpdateVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawWayResponseVO;
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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: qiqi
 **/
@RestController
@Tag(name = "金流-提现配置-提现通道")
@RequestMapping("/withdraw-channel/api")
@AllArgsConstructor
public class SystemWithdrawChannelController {

    private final SystemWithdrawChannelApi systemWithdrawChannelApi;

    private final SystemWithdrawTypeApi systemWithdrawTypeApi;

    private final SystemWithdrawWayApi systemWithdrawWayApi;

    private final VipRankApi vipRankApi;


    @PostMapping("selectPage")
    @Operation(summary = "提现通道分页查询")
    ResponseVO<Page<SystemWithdrawChannelResponseVO>> selectPage(@RequestBody @Validated SystemWithdrawChannelRequestVO withdrawChannelRequestVO){
        return systemWithdrawChannelApi.selectPage(withdrawChannelRequestVO);
    }

    @PostMapping("insert")
    @Operation(summary = "提现通道新增")
    ResponseVO<Void> insert(@RequestBody @Validated SystemWithdrawChannelAddVO withdrawChannelAddVO){
        withdrawChannelAddVO.setOperatorUserNo(CurrReqUtils.getAccount());
        return systemWithdrawChannelApi.insert(withdrawChannelAddVO);
    }

    @PostMapping("update")
    @Operation(summary = "提现通道修改")
    ResponseVO<Void> update(@RequestBody @Validated SystemWithdrawChannelUpdateVO withdrawChannelUpdateVO){
        withdrawChannelUpdateVO.setOperatorUserNo(CurrReqUtils.getAccount());
        return systemWithdrawChannelApi.update(withdrawChannelUpdateVO);
    }

    @PostMapping("selectBySort")
    @Operation(summary = "充值通道排序")
    ResponseVO<List<SystemWithdrawChannelResponseVO>> selectBySort(@RequestBody @Validated SystemWithdrawChannelRequestVO withdrawChannelRequestVO){
        return systemWithdrawChannelApi.selectBySort(withdrawChannelRequestVO);
    }

    @PostMapping("batchSave")
    @Operation(summary = "批量保存充值通道")
    ResponseVO<Boolean> batchSave(@RequestBody List<SortNewReqVO> sortNewReqVOS){
        return systemWithdrawChannelApi.batchSave(CurrReqUtils.getAccount(),sortNewReqVOS);
    }


    @PostMapping("enableOrDisable")
    @Operation(summary = "提现通道启用禁用")
    ResponseVO<Void> enableOrDisable(@RequestBody @Validated SystemWithdrawChannelStatusVO withdrawChannelStatusVO){
        withdrawChannelStatusVO.setOperatorUserNo(CurrReqUtils.getAccount());
        return systemWithdrawChannelApi.enableOrDisable(withdrawChannelStatusVO);
    }

    @Operation(summary = "下拉框 类型从公共下拉框获取: CHANNEL_TYPE")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox(@Validated @RequestBody CurrencyCodeReqVO currencyCodeReqVO) {
        List<CodeValueVO> withdrawWayEnums = Lists.newArrayList();
        ResponseVO<List<SystemWithdrawWayResponseVO>> listResponseVO= systemWithdrawWayApi.selectAll();
        if(listResponseVO.isOk()){
            List<SystemWithdrawWayResponseVO> systemWithdrawWayRespVOS=listResponseVO.getData();
            systemWithdrawWayRespVOS=systemWithdrawWayRespVOS.stream().filter(o->o.getCurrencyCode().equals(currencyCodeReqVO.getCurrencyCode())).collect(Collectors.toUnmodifiableList());
            for(SystemWithdrawWayResponseVO systemWithdrawWayRespVO:systemWithdrawWayRespVOS){
                CodeValueVO codeValueVO=new CodeValueVO();
                codeValueVO.setType(systemWithdrawWayRespVO.getCurrencyCode());
                codeValueVO.setCode(systemWithdrawWayRespVO.getId());
                codeValueVO.setValue(systemWithdrawWayRespVO.getWithdrawWayI18());
                withdrawWayEnums.add(codeValueVO);
            }
        }

        Map<String, List<CodeValueVO>> result = Maps.newHashMap();
        result.put("withdrawWayEnums", withdrawWayEnums);
        return ResponseVO.success(result);
    }
    @Operation(summary = "下拉框 类型从公共下拉框获取: CHANNEL_TYPE")
    @PostMapping(value = "/getVipRankDownBox")
    public ResponseVO<Map<String, List<CodeValueNoI18VO>>> getVipRankDownBox(@Validated @RequestBody CurrencyCodeReqVO currencyCodeReqVO) {
        ResponseVO<List<CodeValueNoI18VO>> vipRankResp=vipRankApi.getVipRank();
        Map<String, List<CodeValueNoI18VO>> result = Maps.newHashMap();
        if(vipRankResp.isOk()){
            result.put("vipRanks", vipRankResp.getData());
        }
        return ResponseVO.success(result);
    }

}
