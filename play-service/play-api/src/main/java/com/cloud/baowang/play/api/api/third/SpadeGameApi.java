package com.cloud.baowang.play.api.api.third;

import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.spade.req.SpadeBalanceReq;
import com.cloud.baowang.play.api.vo.spade.req.SpadeTransferReq;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "spade-api", value = ApiConstants.NAME)
@Tag(name = "Spade")
public interface SpadeGameApi {
    String PREFIX = ApiConstants.PREFIX + "/spade/api/";


    @PostMapping(PREFIX + "getBalance")
    Object getBalance(@RequestBody SpadeBalanceReq vo);

    @PostMapping(PREFIX + "transfer")
    Object transfer(@RequestBody SpadeTransferReq vo);
}
