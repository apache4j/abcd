package com.cloud.baowang.agent.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentMerchantReviewRecordApi;
import com.cloud.baowang.agent.api.vo.merchant.AddMerchantVO;
import com.cloud.baowang.agent.api.vo.merchant.AgentMerchantReviewRecordVO;
import com.cloud.baowang.agent.api.vo.merchant.AuditVO;
import com.cloud.baowang.agent.api.vo.merchant.MerchantReviewRecordPageQueryVO;
import com.cloud.baowang.agent.service.AgentMerchantReviewRecordService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class AgentMerchantReviewRecordApiImpl implements AgentMerchantReviewRecordApi {
    private final AgentMerchantReviewRecordService reviewRecordService;

    @Override
    public ResponseVO<Boolean> addMerchant(AddMerchantVO vo) {
        return reviewRecordService.addMerchant(vo);
    }

    @Override
    public ResponseVO<Page<AgentMerchantReviewRecordVO>> pageQuery(MerchantReviewRecordPageQueryVO queryVO) {
        return reviewRecordService.pageQuery(queryVO);
    }

    @Override
    public ResponseVO<Boolean> lock(String id, String account) {
        return reviewRecordService.lock(id, account);
    }

    @Override
    public ResponseVO<Boolean> unLock(String id, String account) {
        return reviewRecordService.unLock(id, account);
    }

    @Override
    public ResponseVO<Boolean> approveReview(AuditVO auditVO) {
        return reviewRecordService.approveReview(auditVO);
    }

    @Override
    public ResponseVO<Boolean> rejectReview(AuditVO auditVO) {
        return reviewRecordService.rejectReview(auditVO);
    }

    @Override
    public ResponseVO<AgentMerchantReviewRecordVO> detail(String id) {
        return reviewRecordService.detail(id);
    }
}
