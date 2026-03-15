/*
package com.cloud.baowang.admin.controller.agent.withdraw;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentWithdrawReviewRecordApi;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewDetailReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewDetailsVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewRecordPageReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewRecordVO;
import com.cloud.baowang.common.core.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("agent-withdraw-review-record/api")
@Tag(name = "资金-资金审核记录-代理提款审核记录")
public class AgentWithdrawReviewRecordController {

    private final AgentWithdrawReviewRecordApi agentWithdrawReviewRecordApi;



    @Operation(summary = "下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, Object>> getDownBox() {
        // 订单状态
        List<Map<String, Object>> status = DepositWithdrawalOrderStatusEnum.getWithdrawReviewRecordList();

        List<DepositWithdrawalOrderStatusEnum> reviewRecordList = Arrays.asList(new DepositWithdrawalOrderStatusEnum[] { DepositWithdrawalOrderStatusEnum.SUCCEED ,DepositWithdrawalOrderStatusEnum.FIRST_AUDIT_REJECT,  DepositWithdrawalOrderStatusEnum.SECOND_AUDIT_REJECT,
                DepositWithdrawalOrderStatusEnum.THIRD_AUDIT_REJECT,DepositWithdrawalOrderStatusEnum.BACKSTAGE_CANCEL,DepositWithdrawalOrderStatusEnum.WITHDRAW_FAIL}) ;

        List<CodeValueVO> statusList = reviewRecordList.stream()
                .map(item ->
                        CodeValueVO.builder().code(item.getCode()).value(item.getName()).build())
                .toList();
        Map<String, Object> result = Maps.newHashMap();
        result.put("status", statusList);
        return ResponseVO.success(result);
    }



    @Operation(summary = "代理提款审核记录列表")
    @PostMapping("withdrawalReviewRecordPageList")
    public ResponseVO<Page<AgentWithdrawReviewRecordVO>> withdrawalReviewRecordPageList(@RequestBody AgentWithdrawReviewRecordPageReqVO vo){
        return ResponseVO.success(agentWithdrawReviewRecordApi.withdrawalReviewRecordPageList(vo));
    }

    @Operation(summary = "代理提款审核记录详情")
    @PostMapping("withdrawReviewRecordDetail")
    public ResponseVO<AgentWithdrawReviewDetailsVO> withdrawReviewRecordDetail(@RequestBody AgentWithdrawReviewDetailReqVO vo){
        return ResponseVO.success(agentWithdrawReviewRecordApi.withdrawReviewRecordDetail(vo));
    }


}
*/
