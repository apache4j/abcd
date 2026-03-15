package com.cloud.baowang.play.api.api.third;

import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.marbles.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "marbles-api", value = ApiConstants.NAME)
@Tag(name = "IM-弹珠")
public interface MarblesGameApi {
    String PREFIX = ApiConstants.PREFIX + "/marbles/api/";


    @PostMapping(PREFIX + "getBalance")
    MarblesBalanceResp getBalance(@RequestBody MarblesReq req);

    @PostMapping(PREFIX + "getApproval")
    MarblesResp getApproval(@RequestBody MarblesApprovalReq req);

    @PostMapping(PREFIX + "placeBet")
    MarblesPlaceBetResp placeBet(@RequestBody MarblesPlaceBetReq req);

    @PostMapping(PREFIX + "settleBet")
    MarblesRefundResp settleBet(@RequestBody MarblesSettleBetReq req);

    @PostMapping(PREFIX + "refund")
    MarblesRefundResp refund(@RequestBody MarblesRefundReq req);



}
