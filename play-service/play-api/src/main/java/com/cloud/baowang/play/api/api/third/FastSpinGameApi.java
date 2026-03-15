package com.cloud.baowang.play.api.api.third;


import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.fastSpin.req.FSBalanceReq;
import com.cloud.baowang.play.api.vo.fastSpin.req.FSTransferReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient(contextId = "fastSpin-api", value = ApiConstants.NAME)
@Tag(name = "fastSpin-游戏")
public interface FastSpinGameApi {

    String PREFIX = ApiConstants.PREFIX + "/fastSpin/api";



    @Operation(summary = "查詢余额")
    @PostMapping(PREFIX + "/getBalance")
    Object getBalance(@RequestBody FSBalanceReq vo, @RequestHeader("Digest") String digest);

    @Operation(summary = "查詢余额")
    @PostMapping(PREFIX + "/transfer")
    Object transfer(@RequestBody FSTransferReq vo, @RequestHeader("Digest") String digest);

}