package com.cloud.baowang.user.api.api.auth;

import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.ads.UserRechargeEventVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(contextId = "remoteAliAuthApi", value = ApiConstants.NAME)
@Tag(name = "阿里云身份/银行卡验证 - AuthApi")
public interface AliAuthApi {

    String PREFIX = ApiConstants.PREFIX + "/remoteAliAuthApi/api/";

    @PostMapping(PREFIX + "bankVerification")
    @Operation(summary = "银行卡验证")
    boolean bankVerification(@RequestParam("userName") String userName,@RequestParam("bankCard")  String bankCard);


    @PostMapping(PREFIX + "phoneVerify")
    @Operation(summary = "手机验证")
    boolean phoneVerify(@RequestParam("userName") String userName,@RequestParam("phone")  String phone);


}
