package com.cloud.baowang.play.api.api.order;

import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.base.DbPanDaBaseRes;
import com.cloud.baowang.play.api.vo.dbPanDaSport.DbPanDaBalanceReq;
import com.cloud.baowang.play.api.vo.dbPanDaSport.DbPanDaConfirmBetReq;
import com.cloud.baowang.play.api.vo.dbPanDaSport.DbPanDaSportBetReqVO;
import com.cloud.baowang.play.api.vo.dbPanDaSport.DbPanDaSportBetResVO;
import com.cloud.baowang.play.api.vo.sba.SBBaseReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "dbPanDaSportApi", value = ApiConstants.NAME)
@Tag(name = "DB熊猫体育")
public interface DBPanDaSportServiceApi {

    String PREFIX = ApiConstants.PREFIX + "/dbPanDaSportApi/api/";

    @Operation(summary = "获取余额")
    @PostMapping(PREFIX + "getBalance")
    DbPanDaBaseRes<String> getBalance(@RequestBody DbPanDaBalanceReq req);


    @Operation(summary = "账变")
    @PostMapping(PREFIX + "bet")
    DbPanDaBaseRes<DbPanDaSportBetResVO> transfer(@RequestBody DbPanDaSportBetReqVO req);

    @Operation(summary = "确认账变")
    @PostMapping(PREFIX + "confirmBet")
    DbPanDaBaseRes<Void> confirmBet(@RequestBody DbPanDaConfirmBetReq req);


}
