package com.cloud.baowang.play.api.vo.agent;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "代理下会员游戏记录请求入参")
public class AgentUserOrderRecordReqVO extends PageVO {
    @Schema(title = "会员账号")
    private String userAccount;
    @Schema(title = "注单号")
    private String orderId;
    @Schema(title = "货币")
    private String currencyCode;
    @Schema(title = "开始时间")
    private Long startTime;
    @Schema(title = "结束时间")
    private Long endTime;
    @Schema(title = "场馆code")
    private String venueCode;
    @Schema(title = "代理账号", hidden = true)
    private List<String> agentAccounts;

    @Schema(title = "代理账号", hidden = true)
    private List<String> agentIds;
    @Schema(title = "站点编号", hidden = true)
    private String siteCode;

    @Schema(title = "timeZone", hidden = true)
    private String timeZone;
}
