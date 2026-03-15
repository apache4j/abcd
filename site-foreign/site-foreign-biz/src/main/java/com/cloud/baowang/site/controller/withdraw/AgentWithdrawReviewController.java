package com.cloud.baowang.site.controller.withdraw;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentWithdrawReviewApi;
import com.cloud.baowang.agent.api.vo.AgentWithdrawChannelResVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.*;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.enums.UserWithDrawReviewOperationEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("agent-withdraw-review/api")
@Tag(name = "资金-资金审核-代理提款审核")
public class AgentWithdrawReviewController {
    private final SiteCurrencyInfoApi siteCurrencyInfoApi;
    private final SystemParamApi systemParamApi;
    private final AgentWithdrawReviewApi agentWithdrawReviewApi;


    @Operation(summary = "获取代理提现审核下拉框数据")
    @PostMapping("getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {

        ArrayList<String> param = new ArrayList<>();
        param.add(CommonConstant.USER_WITHDRAW_REVIEW_OPERATION);
        param.add(CommonConstant.USER_REVIEW_LOCK_STATUS);
        param.add(CommonConstant.AUDIT_TIME_TYPE);
        ResponseVO<Map<String, List<CodeValueVO>>> responseVO = systemParamApi
                .getSystemParamsByList(param);
        if (responseVO.isOk()) {
            Map<String, List<CodeValueVO>> data = responseVO.getData();
            if (data.containsKey(CommonConstant.USER_WITHDRAW_REVIEW_OPERATION)) {
                //会员提款比代理提款多一个挂单审核，这里移除掉
                List<CodeValueVO> codeValueVOS = data.get(CommonConstant.USER_WITHDRAW_REVIEW_OPERATION);
                codeValueVOS.removeIf(codeValueVO -> codeValueVO.getCode().equals(String.valueOf(UserWithDrawReviewOperationEnum.PENDING_AUDIT.getCode())));
                codeValueVOS.removeIf(codeValueVO -> codeValueVO.getCode().equals(String.valueOf(UserWithDrawReviewOperationEnum.CHECK.getCode())));
                data.put(CommonConstant.USER_WITHDRAW_REVIEW_OPERATION, codeValueVOS);
            }
            //币种下拉
            List<CodeValueVO> currencyDownBox = siteCurrencyInfoApi.getCurrencyListNo(CurrReqUtils.getSiteCode());
            data.put("currency_code", currencyDownBox);
            return ResponseVO.success(data);
        }
        throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
    }

    @Operation(summary = "提款审核列表")
    @PostMapping("withdrawReviewPage")
    public ResponseVO<Page<AgentWithdrawReviewPageResVO>> withdrawReviewPage(@Valid @RequestBody AgentWithdrawReviewPageReqVO
                                                                                     agentWithdrawReviewPageReqVO) {
        agentWithdrawReviewPageReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        agentWithdrawReviewPageReqVO.setOperator(CurrReqUtils.getAccount());
        return ResponseVO.success(agentWithdrawReviewApi.withdrawReviewPage(agentWithdrawReviewPageReqVO));
    }

    @Operation(summary = "审核详情")
    @PostMapping("withdrawReviewDetail")
    public ResponseVO<AgentWithdrawReviewDetailsVO> withdrawReviewDetail(@RequestBody AgentWithdrawReviewDetailReqVO vo) {
        vo.setDataDesensitization(CurrReqUtils.getDataDesensity());
        return ResponseVO.success(agentWithdrawReviewApi.withdrawReviewDetail(vo));
    }

    @Operation(summary = "一审锁定/解锁")
    @PostMapping("oneLockOrUnLock")
    public ResponseVO<Boolean> oneLockOrUnLock(@RequestBody AgentWithdrawReviewLockOrUnLockVO vo) {
        vo.setOperator(CurrReqUtils.getAccount());
        return agentWithdrawReviewApi.oneLockOrUnLock(vo);
    }

    @Operation(summary = "待出款锁定/解锁")
    @PostMapping(value = "paymentLockOrUnLock")
    public ResponseVO<Boolean> paymentLockOrUnLock(@RequestBody AgentWithdrawReviewLockOrUnLockVO vo) {
        vo.setOperator(CurrReqUtils.getAccount());
        return agentWithdrawReviewApi.paymentLockOrUnLock(vo);
    }

    @Operation(summary = "一审成功")
    @PostMapping(value = "oneReviewSuccess")
    public ResponseVO<Boolean> oneReviewSuccess(@RequestBody AgentWithdrawReviewReqVO vo) {
        vo.setOperator(CurrReqUtils.getAccount());
        return agentWithdrawReviewApi.oneReviewSuccess(vo);
    }

    @Operation(summary = "待出款成功")
    @PostMapping(value = "paymentReviewSuccess")
    public ResponseVO<Boolean> paymentReviewSuccess(@RequestBody AgentWithdrawReviewReqVO vo) {
        vo.setOperator(CurrReqUtils.getAccount());
        return agentWithdrawReviewApi.paymentReviewSuccess(vo);
    }

    @Operation(summary = "一审拒绝")
    @PostMapping(value = "oneReviewFail")
    public ResponseVO<Boolean> oneReviewFail(@RequestBody AgentWithdrawReviewReqVO vo) {
        vo.setOperator(CurrReqUtils.getAccount());
        return agentWithdrawReviewApi.oneReviewFail(vo);
    }

    @Operation(summary = "待出款拒绝")
    @PostMapping(value = "paymentReviewFail")
    public ResponseVO<Boolean> paymentReviewFail(@RequestBody AgentWithdrawReviewReqVO vo) {
        vo.setOperator(CurrReqUtils.getAccount());
        return agentWithdrawReviewApi.paymentReviewFail(vo);
    }



    @GetMapping("getChannelByChannelTypeAndReviewId")
    @Operation(summary = "根据通道类型,system_param deposit_withdraw_channel 审核单据id获取通道列表")
    public ResponseVO<List<AgentWithdrawChannelResVO>> getChannelByChannelTypeAndReviewId(@RequestParam("id") String id,
                                                                                          @RequestParam("channelType")String channelType) {
        String siteCode = CurrReqUtils.getSiteCode();
        return agentWithdrawReviewApi.getChannelByChannelTypeAndReviewId( siteCode,channelType, id);

    }

    @Operation(summary = "收款账户详情列表")
    @PostMapping(value = "/getAddressInfoList")
    public ResponseVO<Page<AgentWithdrawReviewAddressResponseVO>> getAddressInfoList(@Valid @RequestBody AgentWithdrawReviewAddressReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return agentWithdrawReviewApi.getAddressInfoList(vo);
    }

}
