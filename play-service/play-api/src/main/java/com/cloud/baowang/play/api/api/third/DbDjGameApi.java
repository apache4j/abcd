package com.cloud.baowang.play.api.api.third;

import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.dbDj.DBBalanceReq;
import com.cloud.baowang.play.api.vo.dbDj.DBBalanceRes;
import com.cloud.baowang.play.api.vo.dbDj.DbDJTransferReq;
import com.cloud.baowang.play.api.vo.dbDj.DbDJTransferRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "db-dj-api", value = ApiConstants.NAME)
@Tag(name = "DB电竞")
public interface DbDjGameApi {
    String PREFIX = ApiConstants.PREFIX + "/dbDj/api/";

    @Operation(summary = "查询余额")
    @PostMapping(PREFIX + "queryBalance")
    DBBalanceRes queryBalance(@RequestBody DBBalanceReq req);

    @Operation(summary = "账变")
    @PostMapping(PREFIX + "transfer")
    DbDJTransferRes transfer(@RequestBody DbDJTransferReq req);







}
