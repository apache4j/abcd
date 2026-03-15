package com.cloud.baowang.user.api.vo.UserDetails;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: kimi
 */
@Data
@Schema(title = "会员详情 Param")
public class SelectUserDetailParam {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;
    @Schema(description = "agentId", hidden = true)
    private String agentId;

    @Schema(description = "timeZone", hidden = true)
    private String timeZone;
    @Schema(description = "agentAccount")
    private String agentAccount;

    @Schema(title = "会员账号")
    private String userAccount;

    @Schema(title = "统计开始时间")
    private Long registerStartTime;

    @Schema(title = "统计结束时间")
    private Long registerEndTime;
}
