package com.cloud.baowang.play.api.api.third;


import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.dg2.req.DGActionVo;
import com.cloud.baowang.play.api.vo.dg2.rsp.DGBaseRsp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "dg2-api", value = ApiConstants.NAME)
@Tag(name = "DG2真人")
public interface DG2GameApi {
    String PREFIX = ApiConstants.PREFIX + "dg2/api/";

    @Operation(summary = "获取余额")
    @PostMapping(PREFIX + "getBalance")
    DGBaseRsp getBalance(@RequestParam("agentName") String agentName, @RequestBody DGActionVo actionVo);

    @Operation(summary = "交易")
    @PostMapping(PREFIX + "transfer")
    DGBaseRsp transfer(@RequestParam("agentName") String agentName,@RequestBody DGActionVo actionVo);

    @Operation(summary = "获取余额")
    @PostMapping(PREFIX + "确定交易")
    DGBaseRsp inform(@RequestParam("agentName") String agentName, @RequestBody DGActionVo actionVo);
}
