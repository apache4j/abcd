/*
package com.cloud.baowang.admin.controller.agent;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.UserTransferAgentApi;
import com.cloud.baowang.agent.api.enums.AgentTransferAuditStatusEnum;
import com.cloud.baowang.agent.api.enums.AgentTransferAuditStepEnum;
import com.cloud.baowang.agent.api.enums.AgentUserLockEnum;
import com.cloud.baowang.agent.api.vo.member.MemberTransferAgentApplyVO;
import com.cloud.baowang.agent.api.vo.member.MemberTransferAuthReqVO;
import com.cloud.baowang.agent.api.vo.member.MemberTransferDetailResVO;
import com.cloud.baowang.agent.api.vo.member.MemberTransferLockReqVO;
import com.cloud.baowang.agent.api.vo.member.MemberTransferReviewPageReqVO;
import com.cloud.baowang.agent.api.vo.member.MemberTransferReviewPageResVO;
import com.cloud.baowang.agent.api.vo.member.MemberTransferUserReqVO;
import com.cloud.baowang.agent.api.vo.member.MemberTransferUserRespVO;
import com.cloud.baowang.common.core.utils.CurrentRequestUtils;
import com.cloud.baowang.common.core.enums.ResultCode;
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
 * 会员转代
 * </p>
 *
 *//*

@Tag(name = "代理-会员转代")
@RestController
@RequestMapping("/userTransferAgent")
public class UserTransferAgentController {

    @Autowired
    private UserTransferAgentApi userTransferAgentApi;


    @Operation(summary = "会员转代申请")
    @PostMapping("/apply")
    public ResponseVO<String> apply(@Valid @RequestBody MemberTransferAgentApplyVO vo) {
        return userTransferAgentApi.apply(vo, CurrentRequestUtils.getCurrentUserAccount());
    }

    @Operation(summary = "会员转代审核分页查询")
    @PostMapping("/listPage")
    public ResponseVO<Page<MemberTransferReviewPageResVO>> listPage(@Valid @RequestBody MemberTransferReviewPageReqVO vo) {
        return userTransferAgentApi.listPage(vo, CurrentRequestUtils.getCurrentUserAccount());
    }

    @Operation(summary = "会员转代锁单/解单")
    @PostMapping("/lockOrder")
    public ResponseVO<?> lockOrder(@Valid @RequestBody MemberTransferLockReqVO vo) {
        return userTransferAgentApi.lockOrder(vo, CurrentRequestUtils.getCurrentUserAccount());
    }

    @Operation(summary = "会员转代详情")
    @PostMapping("/detail")
    public ResponseVO<MemberTransferDetailResVO> detail(@Valid @RequestBody MemberTransferLockReqVO vo) {
        return userTransferAgentApi.detail(vo);
    }

    @Operation(summary = "会员转代审核")
    @PostMapping("/audit")
    public ResponseVO<?> audit(@Valid @RequestBody MemberTransferAuthReqVO vo) {
        return userTransferAgentApi.audit(vo, CurrentRequestUtils.getCurrentUserAccount());
    }
    @Operation(summary = "会员转代会员查询")
    @PostMapping("/queryUser")
    public ResponseVO<MemberTransferUserRespVO> queryUser(@RequestBody MemberTransferUserReqVO vo) {
        if (StringUtils.isEmpty(vo.getUserAccount()) && StringUtils.isEmpty(vo.getUserRegister())){
            return ResponseVO.fail(ResultCode.MISSING_PARAMETERS);
        }
        return userTransferAgentApi.queryUser(vo);
    }

    @Operation(summary = ("下拉框"))
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, Object>> getDownBox() {
        Map<String, Object> result = Maps.newHashMap();
        List<CodeValueVO> auditStep = AgentTransferAuditStepEnum.getList()
                .stream()
                .map(item ->
                        CodeValueVO.builder().code(item.getCode()).value(item.getName()).build())
                .toList();
        result.put("auditStep", auditStep);

        List<CodeValueVO> auditStatus = AgentTransferAuditStatusEnum.getList()
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
        return ResponseVO.success(result);
    }
}
*/
