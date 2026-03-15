/*
package com.cloud.baowang.admin.controller.agent;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentUserOverflowApi;
import com.cloud.baowang.agent.api.enums.AgentClientDeviceEnum;
import com.cloud.baowang.agent.api.enums.AgentUserLockEnum;
import com.cloud.baowang.agent.api.enums.AgentUserOverflowAuditStatusEnum;
import com.cloud.baowang.agent.api.enums.AgentUserOverflowAuditStepEnum;
import com.cloud.baowang.agent.api.vo.member.*;
import com.cloud.baowang.common.core.utils.CurrentRequestUtils;
import com.cloud.baowang.agent.api.enums.AgentTypeEnum;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

*/
/**
 * <p>
 * 会员溢出
 * </p>
 *
 *//*

@Tag(name = "代理-会员溢出")
@RestController
@RequestMapping("/agentUserOverflow")
public class AgentUserOverflowController {

    @Autowired
    private AgentUserOverflowApi agentUserOverflowApi;


    @Operation(description = "会员溢出申请")
    @PostMapping("/apply")
    public ResponseVO<String> apply (@RequestBody @Valid AgentUserOverflowApplyVO agentUserOverflowApplyVO) {
        return agentUserOverflowApi.agentUserOverflowApply(agentUserOverflowApplyVO, CurrentRequestUtils.getCurrentUserAccount());
    }

    @Operation(description = "会员溢出审核分页查询")
    @PostMapping("/listPage")
    public ResponseVO<Page<MemberOverflowReviewPageResVO>> listPage(@Valid @RequestBody MemberOverflowReviewPageReqVO vo) {
        return agentUserOverflowApi.agentUserOverflowListPage(vo, CurrentRequestUtils.getCurrentUserAccount());
    }

    @Operation(description = "会员溢出锁单/解单")
    @PostMapping("/lockOrder")
    public ResponseVO<?> lockOrder(@Valid @RequestBody MemberOverflowLockReqVO vo) {
        return agentUserOverflowApi.agentUserOverflowLockOrder(vo, CurrentRequestUtils.getCurrentUserAccount());
    }

    @Operation(description = "会员溢出详情")
    @PostMapping("/detail")
    public ResponseVO<MemberOverflowDetailResVO> detail(@Valid @RequestBody MemberOverflowLockReqVO vo) {
        return agentUserOverflowApi.detail(vo);
    }

    @Operation(description = "会员溢出审核")
    @PostMapping("/audit")
    public ResponseVO<?> audit(@Valid @RequestBody MemberOverflowAuthReqVO vo) {
        return agentUserOverflowApi.audit(vo, CurrentRequestUtils.getCurrentUserAccount());
    }

    @Operation(description = ("下拉框"))
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, Object>> getDownBox() {
        Map<String, Object> result = Maps.newHashMap();
        List<CodeValueVO> auditStep = AgentUserOverflowAuditStepEnum.getList()
                .stream()
                .map(item ->
                        CodeValueVO.builder().code(item.getCode()).value(item.getName()).build())
                .toList();
        result.put("auditStep", auditStep);

        List<CodeValueVO> auditStatus = AgentUserOverflowAuditStatusEnum.getList()
                .stream()
                .map(item ->
                        CodeValueVO.builder().code(item.getCode()).value(item.getName()).build())
                .toList();
        result.put("auditStatus", auditStatus);

        List<CodeValueVO> lockStatus = AgentUserLockEnum.getList()
                .stream()
                .map(item ->
                        CodeValueVO.builder().code(item.getCode()).value(item.getName()).build())
                .toList();
        result.put("lockStatus", lockStatus);

        List<CodeValueVO> deviceType = AgentClientDeviceEnum.getList()
                .stream()
                .map(item ->
                        CodeValueVO.builder().code(String.valueOf(item.getCode())).value(item.getName()).build())
                .toList();
        result.put("device", deviceType);

        List<CodeValueVO> agentType = AgentTypeEnum.getList()
                .stream()
                .map(item ->
                        CodeValueVO.builder().code(item.getCode()).value(item.getName()).build())
                .toList();
        result.put("agentType", agentType);
        return ResponseVO.success(result);
    }
}
*/
