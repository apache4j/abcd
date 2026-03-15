package com.cloud.baowang.play.api.api.third;

import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.third.GameInfoPullReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "mq-record-api", value = ApiConstants.NAME)
@Tag(name = "mq注单相关api")
public interface MqRecordApi {
    String PREFIX = ApiConstants.PREFIX + "/mq-record/api/";

    @Operation(summary = "异常注单处理")
    @PostMapping(PREFIX + "errRecordDeal")
    void errRecordDeal();



}

