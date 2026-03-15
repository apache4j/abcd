package com.cloud.baowang.wallet.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.StatusListVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.UserManualUpReviewApi;
import com.cloud.baowang.wallet.api.vo.WalletReviewListVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.UserManualUpReviewPageVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.UserManualUpReviewResponseVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.UserUpReviewDetailsVO;
import com.cloud.baowang.wallet.service.UserManualUpReviewService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class UserManualUpReviewApiImpl implements UserManualUpReviewApi {

    private UserManualUpReviewService userManualUpReviewService;

    @Override
    public ResponseVO<Boolean> lock(StatusListVO vo, String adminId, String adminName) {
        return userManualUpReviewService.lock(vo, adminId, adminName);
    }

    @Override
    public ResponseVO<Boolean> oneReviewSuccess(WalletReviewListVO vo, String adminId, String adminName) {
        return userManualUpReviewService.oneReviewSuccess(vo, adminId, adminName);
    }

    @Override
    public ResponseVO<Boolean> oneReviewFail(WalletReviewListVO vo, String adminId, String adminName) {
        return userManualUpReviewService.oneReviewFail(vo, adminId, adminName);
    }

    @Override
    public Page<UserManualUpReviewResponseVO> getUpReviewPage(UserManualUpReviewPageVO vo, String adminName) {
        return userManualUpReviewService.getUpReviewPage(vo, adminName);
    }

    @Override
    public ResponseVO<UserUpReviewDetailsVO> getUpReviewDetails(IdVO vo) {
        return userManualUpReviewService.getUpReviewDetails(vo);
    }
    
}
