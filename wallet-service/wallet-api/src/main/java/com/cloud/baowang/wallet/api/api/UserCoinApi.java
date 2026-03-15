package com.cloud.baowang.wallet.api.api;


import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(contextId = "remoteUserCoinApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - userCoin")
public interface UserCoinApi {

    String PREFIX = ApiConstants.PREFIX + "/userCoin/api/";

    @PostMapping(value = PREFIX + "getUserCenterCoin")
    @Operation(summary = "获取中心钱包余额")
    UserCoinWalletVO getUserCenterCoin(@RequestBody UserCoinQueryVO userCoinQueryVO);

    @PostMapping(value = PREFIX + "addCoin")
    @Operation(summary = "添加账变")
    CoinRecordResultVO addCoin(@RequestBody UserCoinAddVO userCoinAddVO) ;

    @PostMapping(value = PREFIX + "getUserCenterCoinAndPlatform")
    @Operation(summary = "获取中心钱包余额包括平台币，siteCode 必须传")
    UserCoinWalletVO getUserCenterCoinAndPlatform(@RequestBody UserCoinQueryVO userCoinQueryVO);


    @PostMapping(value = PREFIX + "getUserActualBalance")
    @Operation(summary = "获取中心钱包余额<实际的(可能为负数)>")
    UserCoinWalletVO getUserActualBalance(@RequestBody UserCoinQueryVO userCoinQueryVO);


    @PostMapping(value = PREFIX + "getUserCenterCoinList")
    @Operation(summary = "获取中心钱包余额")
    List<UserCoinWalletVO> getUserCenterCoinList(@RequestBody List<String> userIds);



}
