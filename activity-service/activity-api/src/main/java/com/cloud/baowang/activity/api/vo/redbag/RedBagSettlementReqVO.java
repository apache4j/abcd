package com.cloud.baowang.activity.api.vo.redbag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "红包雨结算请求信息")
@AllArgsConstructor
@NoArgsConstructor
public class RedBagSettlementReqVO {
    @Schema(description = "场次id")
    private String redbagSessionId;
    @Schema(description = "会员账号", hidden = true)
    private String userAccount;
    @Schema(description = "会员id", hidden = true)
    private String userId;
    @Schema(description = "站点编码", hidden = true)
    private String siteCode;
}
