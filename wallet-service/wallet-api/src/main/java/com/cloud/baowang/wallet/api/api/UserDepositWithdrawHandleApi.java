package com.cloud.baowang.wallet.api.api;


import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.report.DepositWtihdrawMqSendVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(contextId = "remoteUserDepositWithdrawHandleApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - UserDepositWithdrawHandleApi")
public interface UserDepositWithdrawHandleApi {

    String PREFIX = ApiConstants.PREFIX + "/userDepositWithdrawHandleApi/api/";

    @PostMapping(value = PREFIX + "rechargeMq")
    @Operation(summary = "代理代存，更新会员表")
    void rechargeMq(@RequestBody DepositWtihdrawMqSendVO vo);


}
