package com.cloud.baowang.agent.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.UserTransferAgentApi;
import com.cloud.baowang.agent.api.vo.agentreview.UserAccountUpdateVO;
import com.cloud.baowang.agent.api.vo.member.*;
import com.cloud.baowang.agent.service.UserTransferAgentService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 会员转代
 */
@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class UserTransferAgentApiImpl implements UserTransferAgentApi {

    private UserTransferAgentService userTransferAgentService;

    @Override
    public ResponseVO<Boolean> apply(MemberTransferAgentApplyVO vo, String adminName) {
        return ResponseVO.success(userTransferAgentService.apply(vo, adminName));
    }

    @Override
    public ResponseVO<Page<MemberTransferReviewPageResVO>> listPage(MemberTransferReviewPageReqVO vo, String adminName) {
        return ResponseVO.success(userTransferAgentService.listPage(vo, adminName));
    }

    @Override
    public ResponseVO<?> lockOrder(MemberTransferLockReqVO vo, String adminName) {
        return ResponseVO.success(userTransferAgentService.lockOrder(vo, adminName));
    }

    @Override
    public ResponseVO<MemberTransferDetailResVO> detail(MemberTransferLockReqVO vo) {
        return userTransferAgentService.detail(vo);
    }

    @Override
    public ResponseVO<?> audit(MemberTransferAuthReqVO vo, String adminName) {
        return userTransferAgentService.audit(vo, adminName);
    }

    @Override
    public ResponseVO<MemberTransferUserRespVO> queryUser(MemberTransferUserReqVO vo) {
        return userTransferAgentService.queryUser(vo);
    }

    @Override
    public List<ReportUserTransferRespVO> queryUserTransferCount(ReportUserTransferReqVO vo) {
        if (StringUtils.isNotBlank(vo.getSiteCode())) {
            return userTransferAgentService.queryUserTransferCount(vo);
        } else {
            return userTransferAgentService.queryUserTransferCountAllPlatForm(vo);
        }

    }

    @Override
    public List<ReportUserTransferRespVO> queryUserTransferCountAllPlatForm(ReportUserTransferReqVO vo) {
        return userTransferAgentService.queryUserTransferCountAllPlatForm(vo);
    }

    @Override
    public UserAccountUpdateVO getPendingCountBySiteCode(String siteCode) {
        return userTransferAgentService.queryPendingCountBySiteCode(siteCode);
    }

    @Override
    public List<MemberTransferReviewPageResVO> getRecordListByAccounts(MemberTransferReviewPageReqVO memberTransferReviewPageReqVO) {
        return userTransferAgentService.getRecordListByAccounts(memberTransferReviewPageReqVO);
    }

    @Override
    public Long getTotal(MemberTransferReviewPageReqVO vo) {
        return userTransferAgentService.getTotal(vo);
    }

    @Override
    public Page<MemberTransferReviewPageResVO>  listByAuditTime(MemberTransferReviewPageReqVO vo) {
        return userTransferAgentService.listByAuditTime(vo);
    }


}
