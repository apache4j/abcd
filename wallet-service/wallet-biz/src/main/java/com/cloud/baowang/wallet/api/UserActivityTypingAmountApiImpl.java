package com.cloud.baowang.wallet.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.api.UserActivityTypingAmountApi;
import com.cloud.baowang.wallet.api.vo.activity.UserActivityTypingAmountResp;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.UserActivityTypingAmountVO;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.UserActivityTypingChangeVO;
import com.cloud.baowang.wallet.service.UserActivityTypingAmountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class UserActivityTypingAmountApiImpl implements UserActivityTypingAmountApi {
    private final UserActivityTypingAmountService service;

    @Override
    public ResponseVO<Boolean> initUserActivityTypingAmountLimit(UserActivityTypingAmountVO vo) {
        return ResponseVO.success(service.initUserActivityTypingAmountLimit(vo));
    }

    @Override
    public ResponseVO<BigDecimal> getUserActivityTypingAmount(WalletUserInfoVO vo) {
        return ResponseVO.success(service.getUserActivityTypingAmount(vo));
    }

    @Override
    public Boolean checkUserActivityTypingLimit(WalletUserInfoVO vo) {
        return service.checkUserActivityTypingLimit(vo.getUserId(), vo.getSiteCode());
    }

    @Override
    public UserActivityTypingAmountResp getUserActivityTypingLimit(WalletUserInfoVO vo) {
        return service.getUserActivityTypingLimit(vo.getUserId(), vo.getSiteCode());
    }

    @Override
    public ResponseVO<Boolean> updateUserActivityInfo(UserActivityTypingChangeVO vo) {
        return ResponseVO.success(service.updateUserActivityInfo(vo));
    }

    @Override
    public ResponseVO<Boolean> addUserActivityInfo(UserActivityTypingChangeVO vo) {
       return ResponseVO.success(service.addUserActivityInfo(vo));
    }

}
