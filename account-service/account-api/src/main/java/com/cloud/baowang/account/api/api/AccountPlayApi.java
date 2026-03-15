package com.cloud.baowang.account.api.api;

import com.cloud.baowang.account.api.constant.ApiConstants;
import com.cloud.baowang.account.api.vo.AccountCoinResultVO;
import com.cloud.baowang.account.api.vo.AccountTransferReqVO;
import com.cloud.baowang.account.api.vo.AccountUserCoinAddReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ford
 * @date 2025-10-11
 */
@FeignClient(contextId = "accountPlayApi", value = ApiConstants.NAME)
@Tag(name = "账务模块-游戏-接口")

public interface AccountPlayApi {
    String PREFIX = ApiConstants.PREFIX + "/accountPlay/api/";

    @PostMapping(value = PREFIX + "userBetCoin")
    @Operation(summary = "会员投注")
    AccountCoinResultVO userBetCoin(@RequestBody AccountUserCoinAddReqVO betUserCoinAddReqVO);

    //2.派彩
    @PostMapping(value = PREFIX + "userGamePayout")
    @Operation(summary = "会员派彩")
    AccountCoinResultVO userGamePayout(@RequestBody AccountUserCoinAddReqVO gamePayoutUserCoinAddReqVO);

    //3.重算派彩
    @PostMapping(value = PREFIX + "userRecalculateGamePayout")
    @Operation(summary = "会员重算派彩")
    AccountCoinResultVO userRecalculateGamePayout(@RequestBody AccountUserCoinAddReqVO recalculateGamePayoutUserCoinAddReqVO);

    //4.投注取消
    @PostMapping(value = PREFIX + "userBetCancelCoin")
    @Operation(summary = "投注取消")
    AccountCoinResultVO userBetCancelCoin(@RequestBody AccountUserCoinAddReqVO betCancelUserCoinAddReqVO);


}
