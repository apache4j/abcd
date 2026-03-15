package com.cloud.baowang.account.api.api;

import com.cloud.baowang.account.api.constant.ApiConstants;
import com.cloud.baowang.account.api.vo.AccountCoinResultVO;
import com.cloud.baowang.account.api.vo.AccountUserPlatformCoinAddReqVO;
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
@FeignClient(contextId = "accountPlatformCoinApi", value = ApiConstants.NAME)
@Tag(name = "账务模块-平台币-接口")
public interface AccountPlatformCoinApi {

    //1 平台币余额账变
    String PREFIX = ApiConstants.PREFIX + "/accountPlatform/api/";

    @PostMapping(value = PREFIX + "platformCoinAdd")
    @Operation(summary = "平台币余额账变")
    AccountCoinResultVO platformCoinAdd(@RequestBody AccountUserPlatformCoinAddReqVO accountUserPlatformCoinAddReqVO) ;


}
