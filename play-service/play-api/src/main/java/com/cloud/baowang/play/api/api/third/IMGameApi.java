package com.cloud.baowang.play.api.api.third;

import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.im.ImReq;
import com.cloud.baowang.play.api.vo.im.ImResp;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(contextId = "im-api", value = ApiConstants.NAME)
@Tag(name = "IM")
public interface IMGameApi {
    String PREFIX = ApiConstants.PREFIX + "/im/api/";

    @PostMapping(PREFIX + "getBalance")
    ImResp getBalance(ImReq request);

    @PostMapping(PREFIX + "writeBet")
    ImResp writeBet(ImReq request);


}
