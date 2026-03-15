package com.cloud.baowang.play.api.api.third;


import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.omg.OmgReq;
import com.cloud.baowang.play.api.vo.omg.OmgResp;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "omg-api", value = ApiConstants.NAME)
@Tag(name = "omg-游戏")
public interface OMGGameApi {
    String PREFIX = ApiConstants.PREFIX + "/omg/api/";

    @PostMapping(PREFIX + "verify")
    OmgResp verify(@RequestBody OmgReq req);

    @PostMapping(PREFIX + "getBalance")
    OmgResp getBalance(@RequestBody OmgReq req);

    @PostMapping(PREFIX + "changeBalance")
    OmgResp changeBalance(@RequestBody OmgReq req);

}
