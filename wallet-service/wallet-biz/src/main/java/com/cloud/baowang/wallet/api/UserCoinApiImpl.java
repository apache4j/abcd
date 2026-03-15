package com.cloud.baowang.wallet.api;


import com.cloud.baowang.wallet.api.api.UserCoinApi;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.service.UserCoinService;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@AllArgsConstructor
public class UserCoinApiImpl implements UserCoinApi {

    private final UserCoinService userCoinService;


    @Override
    public UserCoinWalletVO getUserCenterCoin(UserCoinQueryVO userCoinQueryVO) {
        return userCoinService.getUserCenterCoin(userCoinQueryVO);
    }

    @Override
    public CoinRecordResultVO addCoin(UserCoinAddVO userCoinAddVO) {
        return userCoinService.addCoin(userCoinAddVO);
    }



    @Override
    public UserCoinWalletVO getUserCenterCoinAndPlatform(UserCoinQueryVO userCoinQueryVO) {
        return userCoinService.getUserCenterCoinAndPlatform(userCoinQueryVO);
    }

    @Override
    public UserCoinWalletVO getUserActualBalance(UserCoinQueryVO userCoinQueryVO) {
        return userCoinService.getUserActualBalance(userCoinQueryVO);
    }

    @Override
    public List<UserCoinWalletVO> getUserCenterCoinList(List<String> userIds) {
        return userCoinService.getUserCenterCoinList(userIds);
    }
}
