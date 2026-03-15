package com.cloud.baowang.wallet.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.UserPlatformCoinManualUpDownApi;
import com.cloud.baowang.wallet.api.vo.fundadjust.GetUserBalanceQueryVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.GetUserBalanceVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualAccountResponseVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualAccountResultVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualDownAccountResponseVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualDownAccountResultVO;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualDownSubmitVO;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualUpSubmitVO;
import com.cloud.baowang.wallet.service.UserPlatformCoinManualUpDownService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;


@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class UserPlatformCoinManualUpDownApiImpl implements UserPlatformCoinManualUpDownApi {

    private UserPlatformCoinManualUpDownService userPlatformCoinManualUpDownService;

    @Override
    public ResponseVO<Boolean> savePlatformCoinManualUp(UserPlatformCoinManualUpSubmitVO vo) {
        return userPlatformCoinManualUpDownService.savePlatformCoinManualUp(vo);
    }

    @Override
    public ResponseVO<Boolean> savePlatformCoinManualDown(UserPlatformCoinManualDownSubmitVO vo) {
        return userPlatformCoinManualUpDownService.savePlatformCoinManualDown(vo);
    }

    @Override
    public ResponseVO<GetUserBalanceVO> getUserBalance(GetUserBalanceQueryVO vo) {
        return userPlatformCoinManualUpDownService.getUserBalance(vo);
    }

    @Override
    public ResponseVO<UserManualAccountResponseVO> checkUpUserAccountInfo(List<UserManualAccountResultVO> list) {
        return userPlatformCoinManualUpDownService.checkUpUserAccountInfo(list);
    }

    @Override
    public ResponseVO<UserManualDownAccountResponseVO> checkDownUserAccountInfo(List<UserManualDownAccountResultVO> list) {
        return userPlatformCoinManualUpDownService.checkDownUserAccountInfo(list);
    }

    @Override
    public BigDecimal getPlatManualUpDownAmount(String userId) {
        return userPlatformCoinManualUpDownService.getPlatManualUpDownAmount(userId);
    }

}
