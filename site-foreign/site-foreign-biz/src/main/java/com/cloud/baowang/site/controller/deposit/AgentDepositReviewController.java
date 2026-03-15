package com.cloud.baowang.site.controller.deposit;


import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentDepositReviewApi;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositReviewPageReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositReviewPageResVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositReviewReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewLockOrUnLockVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.SystemRechargeWayApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("agent-deposit-review/api")
@Tag(name = "资金-资金审核-代理人工存款")
public class AgentDepositReviewController {
    private final SiteCurrencyInfoApi currencyInfoApi;
    private final SystemParamApi systemParamApi;
    private final SystemRechargeWayApi wayApi;
    private final AgentDepositReviewApi agentDepositReviewApi;


    @Operation(summary = "获取代理存款审核下拉框数据")
    @PostMapping("getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {

        ArrayList<String> param = new ArrayList<>();
        param.add(CommonConstant.USER_REVIEW_LOCK_STATUS);
        ResponseVO<Map<String, List<CodeValueVO>>> responseVO = systemParamApi.getSystemParamsByList(param);
        if (responseVO.isOk()) {
            Map<String, List<CodeValueVO>> data = responseVO.getData();
            List<CodeValueVO> codeValueVOS = data.get(CommonConstant.DEPOSIT_WITHDRAW_STATUS);
            if (CollectionUtil.isNotEmpty(codeValueVOS)) {
                //订单状态筛选 处理中 21 取消订单 98 失败100 成功101
                List<String> statusList = new ArrayList<>();
                statusList.add(DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode());
                statusList.add(DepositWithdrawalOrderStatusEnum.APPLICANT_CANCEL.getCode());
                statusList.add(DepositWithdrawalOrderStatusEnum.FAIL.getCode());
                statusList.add(DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
                Set<String> statusSet = Set.copyOf(statusList);
                codeValueVOS = codeValueVOS.stream()
                        .filter(codeValue -> statusSet.contains(codeValue.getCode()))
                        .toList();
                data.put(CommonConstant.DEPOSIT_WITHDRAW_STATUS, codeValueVOS);


            }
            List<CodeValueVO> currencyDownBox = currencyInfoApi.getCurrencyListNo(CurrReqUtils.getSiteCode());
            currencyDownBox = currencyDownBox.stream()
                    //过滤平台币
                    .filter(codeValueVO -> !CommonConstant.PLAT_CURRENCY_CODE.equals(codeValueVO.getCode()))
                    .collect(Collectors.toList());
            data.put("currency_code", currencyDownBox);
            //充值方式
            List<CodeValueVO> rechargeWayListBySiteCode = wayApi.getRechargeWayListBySiteCode(CurrReqUtils.getSiteCode());
            if (CollectionUtil.isNotEmpty(rechargeWayListBySiteCode)) {
                data.put("deposit_withdraw_way", rechargeWayListBySiteCode);
            }
        }
        return responseVO;
    }

    @Operation(summary = "存款款审核列表")
    @PostMapping("depositReviewPage")
    public ResponseVO<Page<AgentDepositReviewPageResVO>> depositReviewPage(@Valid @RequestBody AgentDepositReviewPageReqVO agentDepositReviewPageReqVO) {
        agentDepositReviewPageReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        agentDepositReviewPageReqVO.setOperator(CurrReqUtils.getAccount());
        return ResponseVO.success(agentDepositReviewApi.depositReviewPage(agentDepositReviewPageReqVO));
    }



    @Operation(summary = "锁定/解锁")
    @PostMapping(value = "lockOrUnLock")
    public ResponseVO<Boolean> lockOrUnLock(@RequestBody AgentWithdrawReviewLockOrUnLockVO vo) {
        vo.setOperator(CurrReqUtils.getAccount());
        return agentDepositReviewApi.lockOrUnLock(vo);
    }

    @Operation(summary = "充值审核成功")
    @PostMapping(value = "paymentReviewSuccess")
    public ResponseVO<Boolean> paymentReviewSuccess(@RequestBody @Valid  AgentDepositReviewReqVO vo) {
        vo.setOperator(CurrReqUtils.getAccount());
        return agentDepositReviewApi.paymentReviewSuccess(vo);
    }

    @Operation(summary = "充值审核拒绝")
    @PostMapping(value = "paymentReviewFail")
    public ResponseVO<Boolean> paymentReviewFail(@RequestBody @Valid AgentDepositReviewReqVO vo) {
        vo.setOperator(CurrReqUtils.getAccount());
        return agentDepositReviewApi.paymentReviewFail(vo);
    }

}
