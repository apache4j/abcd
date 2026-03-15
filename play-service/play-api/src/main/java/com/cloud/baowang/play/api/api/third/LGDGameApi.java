package com.cloud.baowang.play.api.api.third;

import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.ldg.LgdResp;
import com.cloud.baowang.play.api.vo.ldg.RequestVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "lgd-api", value = ApiConstants.NAME)
@Tag(name = "LGD")
public interface LGDGameApi {
    String PREFIX = ApiConstants.PREFIX + "/lgd/api/";

    @PostMapping(PREFIX + "oauth")
    LgdResp oauth(@RequestBody RequestVO request);


    @PostMapping(PREFIX + "checkBalance")
    LgdResp checkBalance(@RequestBody RequestVO request);

    @PostMapping(PREFIX + "bet")
    LgdResp bet(@RequestBody RequestVO request);


    @PostMapping(PREFIX + "errorBet")
    LgdResp errorBet(@RequestBody RequestVO request);



}
