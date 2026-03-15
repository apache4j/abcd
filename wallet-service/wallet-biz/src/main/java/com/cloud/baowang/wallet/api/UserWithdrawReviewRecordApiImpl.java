package com.cloud.baowang.wallet.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.wallet.api.api.UserWithdrawReviewRecordApi;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawReviewDetailsVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawReviewRecordPageReqVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawReviewRecordVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.WithdrawReviewDetailReqVO;
import com.cloud.baowang.wallet.service.UserWithdrawReviewRecordService;
import com.cloud.baowang.wallet.service.UserWithdrawReviewService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class UserWithdrawReviewRecordApiImpl implements UserWithdrawReviewRecordApi {

    private final UserWithdrawReviewRecordService userWithdrawReviewRecordService;

    private final UserWithdrawReviewService userWithdrawReviewService;

    @Override
    public Page<UserWithdrawReviewRecordVO> withdrawalReviewRecordPageList(UserWithdrawReviewRecordPageReqVO vo) {
        return userWithdrawReviewRecordService.withdrawalReviewRecordPageList(vo);
    }

    @Override
    public Long withdrawalReviewRecordPageListCount(UserWithdrawReviewRecordPageReqVO vo) {
        return userWithdrawReviewRecordService.withdrawalReviewRecordPageListCount(vo);
    }

    @Override
    public UserWithdrawReviewDetailsVO withdrawReviewRecordDetail(WithdrawReviewDetailReqVO vo) {
        return userWithdrawReviewService.withdrawReviewDetail(vo);
    }
}
