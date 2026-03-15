package com.cloud.baowang.wallet.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.StatusListVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.UserPlatformCoinManualUpReviewApi;
import com.cloud.baowang.wallet.api.vo.WalletReviewListVO;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualUpReviewPageVO;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualUpReviewResponseVO;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinUpReviewDetailsVO;
import com.cloud.baowang.wallet.service.UserPlatformCoinManualUpReviewService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class UserPlatformCoinManualUpReviewApiImpl implements UserPlatformCoinManualUpReviewApi {

    private UserPlatformCoinManualUpReviewService userPlatformCoinManualUpReviewService;

    @Override
    public ResponseVO<Boolean> lock(StatusListVO vo, String adminId, String adminName) {
        return userPlatformCoinManualUpReviewService.lock(vo, adminId, adminName);
    }

    @Override
    public ResponseVO<Boolean> oneReviewSuccess(WalletReviewListVO vo, String adminId, String adminName) {
        return userPlatformCoinManualUpReviewService.oneReviewSuccess(vo, adminId, adminName);
    }

    @Override
    public ResponseVO<Boolean> oneReviewFail(WalletReviewListVO vo, String adminId, String adminName) {
        return userPlatformCoinManualUpReviewService.oneReviewFail(vo, adminId, adminName);
    }

    @Override
    public Page<UserPlatformCoinManualUpReviewResponseVO> getUpReviewPage(UserPlatformCoinManualUpReviewPageVO vo, String adminName) {
        return userPlatformCoinManualUpReviewService.getUpReviewPage(vo, adminName);
    }

    @Override
    public ResponseVO<UserPlatformCoinUpReviewDetailsVO> getUpReviewDetails(IdVO vo) {
        return userPlatformCoinManualUpReviewService.getUpReviewDetails(vo);
    }

}
