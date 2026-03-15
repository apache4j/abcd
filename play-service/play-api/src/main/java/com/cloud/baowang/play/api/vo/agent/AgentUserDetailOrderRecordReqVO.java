package com.cloud.baowang.play.api.vo.agent;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "代理下会员游戏记录请求入参")
public class AgentUserDetailOrderRecordReqVO extends PageVO {
    @Schema(title = "会员id")
    private String userAccount;
    @Schema(title = "开始时间")
    private Long startTime;
    @Schema(title = "结束时间")
    private Long endTime;
    @Schema(title = "场馆code")
    private String venueCode;

    @Schema(title = "siteCode", hidden = true)
    private String siteCode;
    @Schema(title = "代理账号", hidden = true)
    private String agentAccount;

    @Schema(title = "代理id", hidden = true)
    private String agentId;
    @Schema(title = "timeZone", hidden = true)
    private String timeZone;
}
