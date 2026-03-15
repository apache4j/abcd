package com.cloud.baowang.play.api.api.third;


import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.db.acelt.vo.BalanceQueryVO;
import com.cloud.baowang.play.api.vo.db.acelt.vo.TransferCheckVO;
import com.cloud.baowang.play.api.vo.db.acelt.vo.TransferRequestVO;
import com.cloud.baowang.play.api.vo.db.acelt.vo.TransferRspData;
import com.cloud.baowang.play.api.vo.db.rsp.acelt.DBAceltBaseRsp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "db-acelt-api", value = ApiConstants.NAME)
@Tag(name = "db彩票")
public interface DBAceltGameApi {

    String PREFIX = ApiConstants.PREFIX + "db/acelt/api/";

    @Operation(summary = "获取余额")
    @PostMapping(PREFIX + "getBalance")
    DBAceltBaseRsp getBalance(@RequestBody BalanceQueryVO reqVO);

    @Operation(summary = "获取余额")
    @PostMapping(PREFIX + "upateBalance")
    DBAceltBaseRsp upateBalance(@RequestBody TransferRequestVO reqVO);

    @Operation(summary = "获取余额")
    @PostMapping(PREFIX + "transfer")
    DBAceltBaseRsp transfer(@RequestBody TransferCheckVO reqVO);

    @Operation(summary = "获取余额")
    @PostMapping(PREFIX + "safetyTransfer")
    TransferRspData safetyTransfer(@RequestBody TransferCheckVO reqVO);
}
