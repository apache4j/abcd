package com.cloud.baowang.wallet.api.vo.agent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description ="代理统计下线查询统一入参")
public class WalletAgentSubLineReqVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description ="代理账号列表")
    private List<String> agentAccountList;
    @Schema(description ="代理Id列表")
    private List<String> agentIds;
    @Schema(description ="统计开始时间")
    private Long startTime;
    @Schema(description ="统计结束时间")
    private Long endTime;
    @Schema(description ="资金操作类型 1存款 2取款，查询存取款用")
    private Integer fundsOperateType;
    @Schema(description ="资金操作类型 1加额 2减额，查询人工加减款用")
    private Integer manualOperateType;
    @Schema(description ="资金操作类型 查询人工加减款类型用")
    private List<String> manualAdjustTypes;

    @Schema(description ="货币选择")
    private String currency;

    private String siteCode;
}
