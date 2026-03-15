package com.cloud.baowang.websocket.api.vo.activity.redbag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WSRedBagGrabReqVO {
    @Schema(title = "红包场次id")
    private String redbagSessionId;
}
