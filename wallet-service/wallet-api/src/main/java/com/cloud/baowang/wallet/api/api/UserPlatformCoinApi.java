package com.cloud.baowang.wallet.api.api;


import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserPlatformBalanceRespVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserPlatformCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserPlatformCoinWalletVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.userwallet.UserPlatformTransferVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteUserPlatformCoinApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - userPlatformCoin")
public interface UserPlatformCoinApi {

    String PREFIX = ApiConstants.PREFIX + "/userPlatformCoin/api/";

    @PostMapping(value = PREFIX + "addCoin")
    @Operation(summary = "添加平台币账变")
    CoinRecordResultVO addPlatformCoin(@RequestBody UserPlatformCoinAddVO userPlatformCoinAddVO) ;


    @PostMapping(value = PREFIX + "getUserPlatformCoin")
    @Operation(summary = "获取会员平台币余额")
    UserPlatformCoinWalletVO getUserPlatformCoin(@RequestBody UserCoinQueryVO userCoinQueryVO);

    @PostMapping(value = PREFIX + "getUserPlatformBalance")
    @Operation(summary = "获取会员平台币余额-全信息")
    UserPlatformBalanceRespVO getUserPlatformBalance(@RequestBody UserCoinQueryVO userCoinQueryVO);


    @PostMapping(value = PREFIX + "transfer")
    @Operation(summary = "平台币兑换至法币")
    ResponseVO<String> transfer(@RequestBody UserPlatformTransferVO userPlatformTransferVO);



}
