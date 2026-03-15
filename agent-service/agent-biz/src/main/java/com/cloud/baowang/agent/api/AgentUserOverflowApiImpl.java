package com.cloud.baowang.agent.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentUserOverflowApi;
import com.cloud.baowang.agent.api.vo.member.AgentUserOverflowApplyVO;
import com.cloud.baowang.agent.api.vo.member.AgentUserOverflowClientApplyVO;
import com.cloud.baowang.agent.api.vo.member.MemberOverflowAuthReqVO;
import com.cloud.baowang.agent.api.vo.member.MemberOverflowClientPageReqVO;
import com.cloud.baowang.agent.api.vo.member.MemberOverflowClientPageResVO;
import com.cloud.baowang.agent.api.vo.member.MemberOverflowDetailResVO;
import com.cloud.baowang.agent.api.vo.member.MemberOverflowLockReqVO;
import com.cloud.baowang.agent.api.vo.member.MemberOverflowReviewPageReqVO;
import com.cloud.baowang.agent.api.vo.member.MemberOverflowReviewPageResVO;
import com.cloud.baowang.agent.api.vo.member.MemberTransferUserReqVO;
import com.cloud.baowang.agent.api.vo.member.MemberTransferUserRespVO;
import com.cloud.baowang.agent.service.AgentUserOverflowService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 会员溢出
 */
@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class AgentUserOverflowApiImpl implements AgentUserOverflowApi {

    private final AgentUserOverflowService userTransferAgentService;

    @Override
    public ResponseVO<Boolean> agentUserOverflowApply(AgentUserOverflowApplyVO vo, String adminName) {
        return ResponseVO.success(userTransferAgentService.apply(vo, adminName));
    }

    @Override
    public ResponseVO<Page<MemberOverflowReviewPageResVO>> agentUserOverflowListPage(MemberOverflowReviewPageReqVO vo, String adminName) {
        return ResponseVO.success(userTransferAgentService.listPage(vo, adminName));
    }

    @Override
    public ResponseVO<?> agentUserOverflowLockOrder(MemberOverflowLockReqVO vo, String adminName) {
        return ResponseVO.success(userTransferAgentService.lockOrder(vo, adminName));
    }

    @Override
    public ResponseVO<MemberOverflowDetailResVO> detail(MemberOverflowLockReqVO vo) {
        return userTransferAgentService.detail(vo);
    }

    @Override
    public ResponseVO<?> audit(MemberOverflowAuthReqVO vo, String adminName) {
        return userTransferAgentService.audit(vo, adminName);
    }

    @Override
    public ResponseVO<Page<MemberOverflowClientPageResVO>> clientListPage(MemberOverflowClientPageReqVO vo) {
        return userTransferAgentService.clientListPage(vo);
    }

    @Override
    public ResponseVO<?> clientApply(AgentUserOverflowClientApplyVO vo) {
        return userTransferAgentService.clientApply(vo);
    }

    @Override
    public ResponseVO<MemberTransferUserRespVO> queryUser(MemberTransferUserReqVO vo) {
        return userTransferAgentService.queryUser(vo);
    }

    @Override
    public List<MemberOverflowReviewPageResVO> getUserOverflowByAccount(MemberOverflowReviewPageReqVO memberOverflowReviewPageReqVO) {
        return userTransferAgentService.getUserOverflowByAccount(memberOverflowReviewPageReqVO);
    }

    @Override
    public Long getTotal(MemberOverflowReviewPageReqVO vo) {
        return userTransferAgentService.getTotal(vo);
    }

    @Override
    public Page<MemberOverflowReviewPageResVO>  listByAuditTime(MemberOverflowReviewPageReqVO vo) {
        return userTransferAgentService.listByAuditTime(vo);
    }

}
