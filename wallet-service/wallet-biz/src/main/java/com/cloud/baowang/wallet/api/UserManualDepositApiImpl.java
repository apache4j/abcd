package com.cloud.baowang.wallet.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.UserManualDepositApi;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.*;
import com.cloud.baowang.wallet.service.UserDepositReviewService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class UserManualDepositApiImpl implements UserManualDepositApi {

    private final UserDepositReviewService userDepositReviewService;

    @Override
    public Page<UserManualDepositPageResVO> pageList(UserManualDepositPageReqVO vo) {
        return userDepositReviewService.pageList(vo);
    }

    @Override
    public ResponseVO<Boolean> lockOrUnLock(UserManualDepositLockOrUnLockVO vo) {
        return userDepositReviewService.lockOrUnLock(vo);
    }

    @Override
    public ResponseVO<Boolean> paymentReviewSuccess(UserDepositReviewReqVO vo) {
        return userDepositReviewService.paymentReviewSuccess(vo);
    }

    @Override
    public ResponseVO<Boolean> paymentReviewFail(UserDepositReviewReqVO vo) {
        return userDepositReviewService.paymentReviewFail(vo);
    }

    @Override
    public Page<UserManualDepositRecordPageResVO> userManualDepositRecordPage(UserManualDepositRecordPageReqVO vo) {
        return userDepositReviewService.userManualDepositRecordPage(vo);
    }

    @Override
    public ResponseVO<Long> userManualDepositReviewRecordExportCount(UserManualDepositRecordPageReqVO vo) {
        return userDepositReviewService.userManualDepositReviewRecordExportCount(vo);
    }

    @Override
    public ResponseVO<Long> userManualDepositCount(UserManualDepositPageReqVO vo) {
        return userDepositReviewService.userManualDepositCount(vo);
    }
}
