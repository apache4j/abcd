package com.cloud.baowang.play.api.api.third;


import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.nextSpin.NextSpinReq;
import com.cloud.baowang.play.api.vo.nextSpin.NextSpinTransactionRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "nextSpin-api", value = ApiConstants.NAME)
@Tag(name = "nextSpin-游戏")
public interface NextSpinGameApi {
    String PREFIX = ApiConstants.PREFIX + "/nextSpin/api/";

    @Operation(summary = "记录下注为0的注单")
    @PostMapping(PREFIX + "refund")
    void save(@RequestBody NextSpinTransactionRecordVO vo);

    @Operation(summary = "获取")
    @PostMapping(PREFIX + "getByTransferId")
    NextSpinTransactionRecordVO getByTransferId(@RequestParam("transferId") String transferId);



    @Operation(summary = "获取")
    @PostMapping(PREFIX + "oauth")
    Object oauth(@RequestBody NextSpinReq request);

    @Operation(summary = "获取")
    @PostMapping(PREFIX + "checkBalance")
    Object checkBalance(@RequestBody NextSpinReq request);

    @Operation(summary = "获取")
    @PostMapping(PREFIX + "bet")
    Object bet(@RequestBody NextSpinReq request);
}
