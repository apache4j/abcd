package com.cloud.baowang.system.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "代理域名-邀请码vo")
@Data
public class AgentInviteCodeDomainVO {
    @Schema(description = "站点")
    private String siteCode;
    @Schema(description = "邀请码")
    private String inviteCode;
    @Schema(description = "域名")
    private String domainAddr;
    @Schema(description = "FaceBook PixId")
    private String FbPixId;
    @Schema(description = "FaceBook Token")
    private String FbToken;
    @Schema(description = "Google Ads PixId")
    private String GooglePixId;
    @Schema(description = "Google Ads Token")
    private String GoogleToken;
}
