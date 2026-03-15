package com.cloud.baowang.activity.api.vo.redbag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "红包雨活动参与校验vo")
public class RedBagParticipateReqVO {
    @Schema(description = "场次id")
    private String redbagSessionId;
    @Schema(description = "站点code", hidden = true)
    private String siteCode;
    @Schema(description = "会员账号", hidden = true)
    private String userAccount;
    @Schema(description = "会员id", hidden = true)
    private String userId;
    @Schema(description = "站点时区", hidden = true)
    private String timeZone;
    @Schema(description = "设备类型", hidden = true)
    private Integer reqDeviceType;
}
