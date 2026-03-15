package com.cloud.baowang.wallet.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.UserPlatformManualUpReviewRecordApi;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualUpReviewRecordPageVO;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualUpReviewRecordResponseResultVO;
import com.cloud.baowang.wallet.service.UserPlatformCoinManualUpReviewService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class UserPlatformCoinManualUpReviewRecordApiImpl implements UserPlatformManualUpReviewRecordApi {

    private UserPlatformCoinManualUpReviewService userPlatformCoinManualUpReviewService;

    @Override
    public Page<UserPlatformCoinManualUpReviewRecordResponseResultVO> getRecordPage(UserPlatformCoinManualUpReviewRecordPageVO vo) {
        return userPlatformCoinManualUpReviewService.getRecordPage(vo);
    }

    @Override
    public ResponseVO<Long> getTotalCount(UserPlatformCoinManualUpReviewRecordPageVO vo) {
        return userPlatformCoinManualUpReviewService.getTotalCount(vo);
    }
}
