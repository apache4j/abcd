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
@FeignClient(contextId = "accountActivityApi", value = ApiConstants.NAME)
@Tag(name = "账务模块-活动-接口")
public interface AccountActivityApi {
    //1.VIP福利、返水、活动奖励

    String PREFIX = ApiConstants.PREFIX + "/accountActivity/api/";

    @PostMapping(value = PREFIX + "userDeposit")
    @Operation(summary = "活动相关账变")
    AccountCoinResultVO userActivityCoin(@RequestBody AccountUserCoinAddReqVO accountUserCoinAddReqVO);

}
