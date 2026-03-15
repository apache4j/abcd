package com.cloud.baowang.wallet.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.UserWithdrawRecordApi;
import com.cloud.baowang.wallet.api.api.UserWithdrawReviewApi;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawRecordVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawReviewAddressResponseVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawReviewDetailsVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawReviewLockOrUnLockVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawReviewPageReqVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawReviewPageResVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawalRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.WithdrawCancelVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.WithdrawReviewAddressReqVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.WithdrawReviewDetailReqVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.WithdrawReviewReqVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawChannelResVO;
import com.cloud.baowang.wallet.service.UserWithdrawRecordService;
import com.cloud.baowang.wallet.service.UserWithdrawReviewService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class UserWithdrawReviewApiImpl implements UserWithdrawReviewApi {

    private final UserWithdrawReviewService userWithdrawReviewService;


    @Override
    public Page<UserWithdrawReviewPageResVO> withdrawReviewPage(UserWithdrawReviewPageReqVO vo) {
        return userWithdrawReviewService.withdrawReviewPage(vo);
    }

    @Override
    public UserWithdrawReviewDetailsVO withdrawReviewDetail(WithdrawReviewDetailReqVO vo) {
        return userWithdrawReviewService.withdrawReviewDetail(vo);
    }

    @Override
    public ResponseVO<Boolean> oneLockOrUnLock(UserWithdrawReviewLockOrUnLockVO vo) {
        return userWithdrawReviewService.oneLockOrUnLock(vo);
    }

    @Override
    public ResponseVO<Boolean> orderLockOrUnLock(UserWithdrawReviewLockOrUnLockVO vo) {
        return userWithdrawReviewService.orderLockOrUnLock(vo);
    }

    @Override
    public ResponseVO<Boolean> paymentLockOrUnLock(UserWithdrawReviewLockOrUnLockVO vo) {
        return userWithdrawReviewService.paymentLockOrUnLock(vo);
    }

    @Override
    public ResponseVO<Boolean> oneReviewSuccess(WithdrawReviewReqVO vo) {
        return userWithdrawReviewService.oneReviewSuccess(vo);
    }

    @Override
    public ResponseVO<Boolean> oneReviewFail(WithdrawReviewReqVO vo) {
        return userWithdrawReviewService.oneReviewFail(vo);
    }

    @Override
    public ResponseVO<Boolean> oneReviewOrder(WithdrawReviewReqVO vo) {
        return userWithdrawReviewService.oneReviewOrder(vo);
    }

    @Override
    public ResponseVO<Boolean> orderReviewSuccess(WithdrawReviewReqVO vo) {
        return userWithdrawReviewService.orderReviewSuccess(vo);
    }


    @Override
    public ResponseVO<Boolean> orderReviewFail(WithdrawReviewReqVO vo) {
        return userWithdrawReviewService.orderReviewFail(vo);
    }


    @Override
    public ResponseVO<Boolean> paymentReviewSuccess(WithdrawReviewReqVO vo) {
        return userWithdrawReviewService.paymentReviewSuccess(vo);
    }


    @Override
    public ResponseVO<Boolean> paymentReviewFail(WithdrawReviewReqVO vo) {
        return userWithdrawReviewService.paymentReviewFail(vo);
    }

    @Override
    public ResponseVO<List<WithdrawChannelResVO>> getChannelByChannelTypeAndReviewId(String depositWithdrawChannel, String siteCode, String id) {
        return userWithdrawReviewService.getChannelByChannelTypeAndReviewId(depositWithdrawChannel, siteCode, id);
    }

    @Override
    public long getTotalPendingReviewBySiteCode(String siteCode) {
        return userWithdrawReviewService.getTotalPendingReviewBySiteCode(siteCode);
    }

    @Override
    public ResponseVO<Page<UserWithdrawReviewAddressResponseVO>> getAddressInfoList(WithdrawReviewAddressReqVO vo) {
        return userWithdrawReviewService.getAddressInfoList(vo);
    }

}
