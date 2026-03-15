package com.cloud.baowang.play.api.api.third;

import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.tf.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "tf-api", value = ApiConstants.NAME)
@Tag(name = "tf")
public interface TFGameApi {
    String PREFIX = ApiConstants.PREFIX + "/tf/api/";


    @PostMapping(PREFIX + "validate")
    TfValidResp validate(@RequestBody TfValidReq req);

    @PostMapping(PREFIX + "wallet")
    TfWalletResp wallet(@RequestParam("loginName") String loginName);

    @PostMapping(PREFIX + "transfer")
    TfTransferResp transfer(@RequestBody TfTransferReq req);

    @PostMapping(PREFIX + "rollback")
    TfTransferResp rollback(@RequestBody TfTransferReq req);

}
