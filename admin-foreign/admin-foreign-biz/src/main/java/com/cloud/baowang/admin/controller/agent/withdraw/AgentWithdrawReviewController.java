/*
package com.cloud.baowang.admin.controller.agent.withdraw;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentWithdrawReviewApi;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawCancelVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewDetailReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewDetailsVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewLockOrUnLockVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewPageReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewPageResVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewReqVO;
import com.cloud.baowang.admin.utils.CommonAdminUtils;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.system.api.constant.AdminPermissionApiConstant;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.agent.api.enums.AgentWithdrawReviewNumberEnum;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.vo.adminLogin.LoginAdmin;
import com.google.common.collect.Maps;
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

*/
/**
 * @author qiqi
 *//*

@RestController
@AllArgsConstructor
@RequestMapping("agent-withdraw-review/api")
@Tag(name = "资金-资金审核-代理提款审核")

public class AgentWithdrawReviewController {
    
    private final SystemParamApi systemParamApi;

    private final AgentWithdrawReviewApi agentWithdrawReviewApi;
    

    @Operation(summary = "获取代理提现审核下拉框数据")
    @PostMapping("getDownBox")
    public ResponseVO<Map<String, Object>> getDownBox(){

        ResponseVO<Map<String, List< CodeValueVO>>> responseVO = systemParamApi
                .getSystemParamsByList(List.of(CommonConstant.USER_REVIEW_LOCK_STATUS));

        List<String> adminApis = CommonAdminUtils.getAdminApis();
        boolean waitOneReview = false;
        boolean waitTwoReview = false;
        boolean waitThreeReview = false;
        boolean waitWithdraw = false;
        if(null == adminApis ){
             waitOneReview = true;
             waitTwoReview = true;
             waitThreeReview = true;
             waitWithdraw = true;
        }else{
            if (adminApis.contains(AdminPermissionApiConstant.WITHDRAW_WAIT_ONE_REVIEW)) {
                waitOneReview = true;
            }
            if (adminApis.contains(AdminPermissionApiConstant.WITHDRAW_WAIT_TWO_REVIEW)) {
                waitTwoReview = true;
            }
            if (adminApis.contains(AdminPermissionApiConstant.WITHDRAW_WAIT_THIRD_REVIEW)) {
                waitThreeReview = true;
            }
            if (adminApis.contains(AdminPermissionApiConstant.WITHDRAW_WAIT_WITHDRAW)) {
                waitWithdraw = true;
            }
        }
        // 待一审、待二审，待三审，待出款
        List<CodeValueVO> reviewList = new ArrayList<>(AgentWithdrawReviewNumberEnum.getList()
                .stream()
                .map(item ->
                        CodeValueVO.builder().code(item.getCode().toString()).value(item.getName()).build())
                .toList());
        if (!waitOneReview) {
            reviewList.remove(new CodeValueVO(
                    null,
                    AgentWithdrawReviewNumberEnum.WAIT_ONE_REVIEW.getCode().toString(),
                    AgentWithdrawReviewNumberEnum.WAIT_ONE_REVIEW.getName()));
        }
        if (!waitTwoReview) {
            reviewList.remove(new CodeValueVO(
                    null,
                    AgentWithdrawReviewNumberEnum.WAIT_TWO_REVIEW.getCode().toString(),
                    AgentWithdrawReviewNumberEnum.WAIT_TWO_REVIEW.getName()));
        }

        if (!waitThreeReview) {
            reviewList.remove(new CodeValueVO(
                    null,
                    AgentWithdrawReviewNumberEnum.WAIT_THIRD_REVIEW.getCode().toString(),
                    AgentWithdrawReviewNumberEnum.WAIT_THIRD_REVIEW.getName()));
        }
        if (!waitWithdraw) {
            reviewList.remove(new CodeValueVO(
                    null,
                    AgentWithdrawReviewNumberEnum.WAIT_PAY_OUT.getCode().toString(),
                    AgentWithdrawReviewNumberEnum.WAIT_PAY_OUT.getName()));
        }
        Map<String, Object> map = Maps.newHashMap();
        map.put("review_list", reviewList);
        map.putAll(responseVO.getData());
        return ResponseVO.success(map);
    }

    @Operation(summary = "提款审核列表")
    @PostMapping("withdrawReviewPage")
    public ResponseVO<Page<AgentWithdrawReviewPageResVO>> withdrawReviewPage(@Valid @RequestBody AgentWithdrawReviewPageReqVO
                                                                                    agentWithdrawReviewPageReqVO){
        agentWithdrawReviewPageReqVO.setCurrentAdminId(CurrReqUtils.getAccount());
        agentWithdrawReviewPageReqVO.setCurrentAdminName(CurrReqUtils.getAccount());
        return  ResponseVO.success(agentWithdrawReviewApi.withdrawReviewPage(agentWithdrawReviewPageReqVO));
    }

    @Operation(summary = "审核详情")
    @PostMapping("withdrawReviewDetail")
    public ResponseVO<AgentWithdrawReviewDetailsVO> withdrawReviewDetail(@RequestBody AgentWithdrawReviewDetailReqVO vo){
        LoginAdmin loginAdmin = CommonAdminUtils.getLoginAdmin();
        vo.setDataDesensitization(loginAdmin.getDataDesensitization());
        return ResponseVO.success(agentWithdrawReviewApi.withdrawReviewDetail(vo));
    }

    @Operation(summary = "一审锁定/解锁")
    @PostMapping("oneLockOrUnLock")
    public ResponseVO<Boolean> oneLockOrUnLock(@RequestBody AgentWithdrawReviewLockOrUnLockVO vo){
        vo.setCurrentAdminId(CurrReqUtils.getAccount());
        vo.setCurrentAdminName(CurrReqUtils.getAccount());
        return ResponseVO.success(agentWithdrawReviewApi.oneLockOrUnLock(vo));
    }

    @Operation(summary = "二审锁定/解锁")
    @PostMapping(value =  "twoLockOrUnLock")
    public ResponseVO<Boolean> twoLockOrUnLock(@RequestBody AgentWithdrawReviewLockOrUnLockVO vo){
        vo.setCurrentAdminId(CurrReqUtils.getAccount());
        vo.setCurrentAdminName(CurrReqUtils.getAccount());
        return ResponseVO.success(agentWithdrawReviewApi.twoLockOrUnLock(vo));
    }

    @Operation(summary = "三审锁定/解锁")
    @PostMapping(value =  "threeLockOrUnLock")
    public ResponseVO<Boolean> threeLockOrUnLock(@RequestBody AgentWithdrawReviewLockOrUnLockVO vo){
        vo.setCurrentAdminId(CurrReqUtils.getAccount());
        vo.setCurrentAdminName(CurrReqUtils.getAccount());
        return ResponseVO.success(agentWithdrawReviewApi.threeLockOrUnLock(vo));
    }

    @Operation(summary = "待出款锁定/解锁")
    @PostMapping(value = "withdrawAuditLockOrUnLock")
    public ResponseVO<Boolean> withdrawAuditLockOrUnLock(@RequestBody AgentWithdrawReviewLockOrUnLockVO vo){
        vo.setCurrentAdminId(CurrReqUtils.getAccount());
        vo.setCurrentAdminName(CurrReqUtils.getAccount());
        return ResponseVO.success(agentWithdrawReviewApi.withdrawAuditLockOrUnLock(vo));
    }

    @Operation(summary = "一审成功")
    @PostMapping(value =  "oneReviewSuccess")
    public ResponseVO oneReviewSuccess(@RequestBody AgentWithdrawReviewReqVO vo){
        vo.setCurrentAdminId(CurrReqUtils.getAccount());
        vo.setCurrentAdminName(CurrReqUtils.getAccount());
        return agentWithdrawReviewApi.oneReviewSuccess(vo);
    }

    @Operation(summary = "二审成功")
    @PostMapping(value =  "twoReviewSuccess")
    public ResponseVO twoReviewSuccess(@RequestBody AgentWithdrawReviewReqVO vo){
        vo.setCurrentAdminId(CurrReqUtils.getAccount());
        vo.setCurrentAdminName(CurrReqUtils.getAccount());
        return agentWithdrawReviewApi.twoReviewSuccess(vo);
    }

    @Operation(summary = "三审成功")
    @PostMapping(value =  "threeReviewSuccess")
    public ResponseVO threeReviewSuccess(@RequestBody AgentWithdrawReviewReqVO vo){
        vo.setCurrentAdminId(CurrReqUtils.getAccount());
        vo.setCurrentAdminName(CurrReqUtils.getAccount());
        return agentWithdrawReviewApi.threeReviewSuccess(vo);
    }

    @Operation(summary = "一审拒绝")
    @PostMapping(value =  "oneReviewFail")
    public ResponseVO oneReviewFail(@RequestBody AgentWithdrawReviewReqVO vo){
        vo.setCurrentAdminId(CurrReqUtils.getAccount());
        vo.setCurrentAdminName(CurrReqUtils.getAccount());
        return agentWithdrawReviewApi.oneReviewFail(vo);
    }


    @Operation(summary = "二审拒绝")
    @PostMapping(value =  "twoReviewFail")
    public ResponseVO twoReviewFail(@RequestBody AgentWithdrawReviewReqVO vo){
        vo.setCurrentAdminId(CurrReqUtils.getAccount());
        vo.setCurrentAdminName(CurrReqUtils.getAccount());
        return agentWithdrawReviewApi.twoReviewFail(vo);
    }

    @Operation(summary = "三审拒绝")
    @PostMapping(value =  "threeReviewFail")
    public ResponseVO threeReviewFail(@RequestBody AgentWithdrawReviewReqVO vo){
        vo.setCurrentAdminId(CurrReqUtils.getAccount());
        vo.setCurrentAdminName(CurrReqUtils.getAccount());
        return agentWithdrawReviewApi.threeReviewFail(vo);
    }

    @Operation(summary = "出款取消")
    @PostMapping(value =  "withdrawCancel")
    public ResponseVO<Boolean> withdrawCancel(@RequestBody AgentWithdrawCancelVO vo){
        vo.setCurrentAdminId(CurrReqUtils.getAccount());
        vo.setCurrentAdminName(CurrReqUtils.getAccount());
        return ResponseVO.success(agentWithdrawReviewApi.withdrawCancel(vo));
    }

}
*/
