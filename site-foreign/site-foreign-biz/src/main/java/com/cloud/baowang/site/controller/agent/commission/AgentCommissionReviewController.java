package com.cloud.baowang.site.controller.agent.commission;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentCommissionPlanApi;
import com.cloud.baowang.agent.api.api.AgentCommissionReviewApi;
import com.cloud.baowang.agent.api.vo.AdjustCommissionVO;
import com.cloud.baowang.agent.api.vo.agentreview.ReviewListVO;
import com.cloud.baowang.agent.api.vo.commission.*;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.StatusListVO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.site.service.CommonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author: fangfei
 * @createTime: 2024/10/27 0:54
 * @description: 佣金发放审核
 */
@Tag(name = "资金-资金审核-代理佣金发放审核")
@AllArgsConstructor
@RestController
@RequestMapping("/agent-commission-review/api")
@Slf4j
public class AgentCommissionReviewController {

    private final AgentCommissionReviewApi agentCommissionReviewApi;
    private final CommonService commonService;
    public final AgentCommissionPlanApi agentCommissionPlanApi;

    @Operation(summary = "锁单和佣金类型下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        List<String> types = new ArrayList<>();
        types.add(CommonConstant.USER_REVIEW_LOCK_STATUS);
        types.add(CommonConstant.COMMISSION_TYPE);
        types.add(CommonConstant.SETTLE_CYCLE);
        types.add(CommonConstant.COMMISSION_OPERATION_STATUS);
        Map<String, List<CodeValueVO>> systemParamsByList = commonService.getSystemParamsByList(types);
        List<CodeValueVO> valueVOS = systemParamsByList.get(CommonConstant.COMMISSION_OPERATION_STATUS);
        if (valueVOS != null && !valueVOS.isEmpty()) {
            List<CodeValueVO> list = valueVOS.stream()
                    .filter(item -> item.getCode().equals(CommonConstant.business_one_str) || item.getCode().equals(CommonConstant.business_six_str)).toList();
            systemParamsByList.put(CommonConstant.COMMISSION_OPERATION_STATUS, list);
        }
        log.info("AgentCommissionReviewController.getDownBox : "+systemParamsByList);
        return ResponseVO.success(systemParamsByList);
    }

    @Operation(summary = "审核列表")
    @PostMapping(value = "/getReviewPage")
    public ResponseVO<Page<AgentCommissionReviewVO>> getReviewPage(@RequestBody CommissionReviewReq vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setAdminName(CurrReqUtils.getAccount());
        return agentCommissionReviewApi.getReviewPage(vo);
    }

    @Operation(summary = "一审锁单或解锁")
    @PostMapping(value = "/lockCommission")
    public ResponseVO<?> lockCommission(@RequestBody StatusListVO vo) {
        vo.setOperatorName(CurrReqUtils.getAccount());
        return agentCommissionReviewApi.lockCommission(vo);
    }

    @Operation(summary = "二审锁定/解锁")
    @PostMapping("twoLockOrUnLock")
    public ResponseVO<?> twoLockOrUnLock(@RequestBody StatusListVO vo) {
        vo.setOperatorName(CurrReqUtils.getAccount());
        return agentCommissionReviewApi.secondLockOrUnLock(vo);
    }

    @Operation(summary = "佣金审核详情")
    @PostMapping(value = "/getAgentCommissionDetail")
    public ResponseVO<AgentCommissionReviewDetailVO> getAgentCommissionDetail(@RequestBody IdVO idVO) {
        idVO.setSiteCode(CurrReqUtils.getSiteCode());
        return agentCommissionReviewApi.getAgentCommissionDetail(idVO);
    }

    @Operation(summary = "查看佣金方案详情")
    @PostMapping(value = "/getPlanInfoById")
    public ResponseVO<AgentCommissionPlanInfoVO> getPlanInfoById(@RequestBody IdVO idVO) {
        idVO.setSiteCode(CurrReqUtils.getSiteCode());
        return agentCommissionPlanApi.getPlanInfo(idVO);
    }

    @Operation(summary = "一审通过-提交")
    @PostMapping(value = "/oneCommissionReviewSuccess")
    public ResponseVO<?> oneCommissionReviewSuccess(@RequestBody @Validated ReviewListVO vo) {
        vo.setOperatorName(CurrReqUtils.getAccount());
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return agentCommissionReviewApi.oneCommissionReviewSuccess(vo);
    }

    @Operation(summary = "一审拒绝-提交")
    @PostMapping(value = "/oneCommissionReviewFail")
    public ResponseVO<?> oneCommissionReviewFail(@RequestBody @Validated ReviewListVO vo) {
        vo.setOperatorName(CurrReqUtils.getAccount());
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return agentCommissionReviewApi.oneCommissionReviewFail(vo);
    }


    @Operation(summary = "二审通过")
    @PostMapping(value = "/secondReviewSuccess")
    public ResponseVO<?> secondReviewSuccess(@RequestBody @Validated ReviewListVO vo) {
        vo.setOperatorName(CurrReqUtils.getAccount());
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return agentCommissionReviewApi.secondReviewSuccess(vo);
    }

    @Operation(summary = "二审拒绝")
    @PostMapping(value = "/secondReviewRejected")
    public ResponseVO<?> secondReviewRejected(@RequestBody @Validated ReviewListVO vo) {
        vo.setOperatorName(CurrReqUtils.getAccount());
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return agentCommissionReviewApi.secondReviewRejected(vo);
    }

    @Operation(summary = "二审驳回")
    @PostMapping(value = "/secondReviewFail")
    public ResponseVO<?> secondReviewFail(@RequestBody @Validated ReviewListVO vo) {
        vo.setOperatorName(CurrReqUtils.getAccount());
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return agentCommissionReviewApi.secondReviewReturned(vo);
    }


    @Operation(summary = "佣金调整")
    @PostMapping(value = "/adjustCommission")
    public ResponseVO<Boolean> adjustCommission(@RequestBody @Validated AdjustCommissionVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setOperatorName(CurrReqUtils.getAccount());
        return agentCommissionReviewApi.adjustCommission(vo);
    }


}
