package com.cloud.baowang.wallet.api;


import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserPlatformBalanceRespVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserPlatformCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserPlatformCoinWalletVO;
import com.cloud.baowang.wallet.api.api.UserPlatformCoinApi;
import com.cloud.baowang.wallet.api.vo.userwallet.UserPlatformTransferVO;
import com.cloud.baowang.wallet.service.UserPlatformCoinService;
import com.cloud.baowang.wallet.service.UserPlatformCoinTransferService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@AllArgsConstructor
public class UserPlatformCoinApiImpl implements UserPlatformCoinApi {

    private final UserPlatformCoinService userPlatformCoinService;

    private final UserPlatformCoinTransferService userPlatformCoinTransferService;


    @Override
    public CoinRecordResultVO addPlatformCoin(UserPlatformCoinAddVO userPlatformCoinAddVO) {
        return userPlatformCoinService.addPlatformCoin(userPlatformCoinAddVO);
    }

    @Override
    public UserPlatformCoinWalletVO getUserPlatformCoin(UserCoinQueryVO userCoinQueryVO) {
        return userPlatformCoinService.getUserPlatformCoin(userCoinQueryVO);
    }

    @Override
    public UserPlatformBalanceRespVO getUserPlatformBalance(UserCoinQueryVO userCoinQueryVO) {
        return userPlatformCoinService.getUserPlatformBalance(userCoinQueryVO);
    }

    @Override
    public ResponseVO<String> transfer(UserPlatformTransferVO userPlatformTransferVO) {
        return userPlatformCoinTransferService.transfer(userPlatformTransferVO);
    }


}
