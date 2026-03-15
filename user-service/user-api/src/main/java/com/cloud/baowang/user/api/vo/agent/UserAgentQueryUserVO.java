package com.cloud.baowang.user.api.vo.agent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "代理查询对应会员vo")
@Data
public class UserAgentQueryUserVO {
    @Schema(description = "站点")
    private String siteCode;
    @Schema(description = "代理账号")
    private String agentAccount;
    @Schema(description = "代理id")
    private String agentId;
    @Schema(description = "注册时间-开始时间")
    private Long regStartTime;
    @Schema(description = "注册时间-结束时间")
    private Long regEndTime;

}
