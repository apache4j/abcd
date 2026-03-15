package com.cloud.baowang.account.api.api;

import com.cloud.baowang.account.api.constant.ApiConstants;
import com.cloud.baowang.account.api.vo.AccountCoinResultVO;
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
@FeignClient(contextId = "accountUserApi", value = ApiConstants.NAME)
@Tag(name = "账务模块-会员-接口")
public interface AccountUserApi {

    String PREFIX = ApiConstants.PREFIX + "/accountUserCoin/api/";

    @PostMapping(value = PREFIX + "userDeposit")
    @Operation(summary = "会员余额账变")
    AccountCoinResultVO userBalanceCoin(@RequestBody AccountUserCoinAddReqVO accountUserCoinAddReqVO);


    @PostMapping(value = PREFIX + "userWithdraw")
    @Operation(summary = "会员冻结余额账变")
    AccountCoinResultVO userFreezeBalanceCoin(@RequestBody AccountUserCoinAddReqVO accountUserCoinAddReqVO);



}
