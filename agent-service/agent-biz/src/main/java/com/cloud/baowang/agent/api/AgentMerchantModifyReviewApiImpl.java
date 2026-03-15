package com.cloud.baowang.agent.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentMerchantModifyReviewApi;
import com.cloud.baowang.agent.api.vo.merchant.AgentMerchantModifyPageQueryVO;
import com.cloud.baowang.agent.api.vo.merchant.AgentMerchantModifyReviewVO;
import com.cloud.baowang.agent.api.vo.merchant.AuditVO;
import com.cloud.baowang.agent.api.vo.merchant.MerchantModifyVO;
import com.cloud.baowang.agent.service.AgentMerchantModifyReviewService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class AgentMerchantModifyReviewApiImpl implements AgentMerchantModifyReviewApi {
    private final AgentMerchantModifyReviewService reviewService;

    /**
     * 发起变更
     * @param modifyVO
     * @return
     */
    @Override
    public ResponseVO<Boolean> initInfoModify(MerchantModifyVO modifyVO) {
        return reviewService.initInfoModify(modifyVO);
    }

    @Override
    public ResponseVO<Page<AgentMerchantModifyReviewVO>> pageQuery(AgentMerchantModifyPageQueryVO queryVO) {
        return reviewService.pageQuery(queryVO);
    }

    @Override
    public ResponseVO<AgentMerchantModifyReviewVO> detail(String id,String account) {
        return reviewService.detail(id,account);
    }

    @Override
    public ResponseVO<Boolean> lock(String id, String account) {
        return reviewService.lock(id,account);
    }

    @Override
    public ResponseVO<Boolean> unLock(String id, String account) {
        return reviewService.unLock(id,account);
    }

    @Override
    public ResponseVO<Boolean> approveReview(AuditVO auditVO) {
        return reviewService.approveReview(auditVO);
    }

    @Override
    public ResponseVO<Boolean> rejectReview(AuditVO auditVO) {
        return reviewService.rejectReview(auditVO);
    }
}
