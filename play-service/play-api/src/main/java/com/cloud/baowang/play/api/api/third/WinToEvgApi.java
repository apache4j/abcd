package com.cloud.baowang.play.api.api.third;

import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.dbDj.DBBalanceReq;
import com.cloud.baowang.play.api.vo.dbDj.DBBalanceRes;
import com.cloud.baowang.play.api.vo.dbDj.DbDJTransferReq;
import com.cloud.baowang.play.api.vo.dbDj.DbDJTransferRes;
import com.cloud.baowang.play.api.vo.winto.req.WintoBaseVO;
import com.cloud.baowang.play.api.vo.winto.req.WintoTransferVO;
import com.cloud.baowang.play.api.vo.winto.rsp.WinToEvgRsp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "winto-evg-api", value = ApiConstants.NAME)
@Tag(name = "winto电子")
public interface WinToEvgApi {
    String PREFIX = ApiConstants.PREFIX + "/wintoEvg/api/";

    @Operation(summary = "身份验证")
    @PostMapping(PREFIX + "verifySession")
    WinToEvgRsp verifySession(@RequestBody WintoBaseVO req);

    @Operation(summary = "查余额")
    @PostMapping(PREFIX + "getBalance")
    WinToEvgRsp getBalance(@RequestBody WintoBaseVO req);

    @Operation(summary = "下注/派彩")
    @PostMapping(PREFIX + "betTransfer")
    WinToEvgRsp betTransfer(@RequestBody WintoTransferVO req);

    @Operation(summary = "取消下注/派彩")
    @PostMapping(PREFIX + "cancelBetTransfer")
    WinToEvgRsp cancelBetTransfer(@RequestBody WintoTransferVO req);

    @Operation(summary = "调整玩家余额")
    @PostMapping(PREFIX + "adjustment")
    WinToEvgRsp adjustment(@RequestBody WintoTransferVO req);

    @Operation(summary = "人工同步注单")
    @PostMapping(PREFIX + "manualBetTransfer")
    WinToEvgRsp manualBetTransfer(@RequestBody WintoTransferVO req);
}
