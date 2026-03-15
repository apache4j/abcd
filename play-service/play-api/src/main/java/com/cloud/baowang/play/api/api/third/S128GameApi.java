package com.cloud.baowang.play.api.api.third;

import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.s128.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(contextId = "s128-api", value = ApiConstants.NAME)
@Tag(name = "斗鸡")
public interface S128GameApi {
    String PREFIX = ApiConstants.PREFIX + "/s128/api/";



    @PostMapping(PREFIX + "getBalance")
    GetBalanceRes getBalance(GetBalanceReq req);

    @PostMapping(PREFIX + "bet")
    BetRes bet(BetReq req);

    @PostMapping(PREFIX + "cancelBet")
    CancelBetRes cancelBet(CancelBetReq req);


}
