package com.cloud.baowang.agent.api.vo.agentCoin;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;


@Data
@Schema(title = "代理账变明细列表请求对象")
public class AgentCoinChangeReqVO extends PageVO {

    @Schema(description = "钱包类型（1-佣金钱包; 2-代存钱包）")
    @NotNull
    private Integer walletType;

    @Schema(description = "账变类型")
    private Integer coinChangeType;

    @Schema(description = "自定义开始时间")
    @NotNull
    private Long startTime;

    @Schema(description ="自定义结束时间")
    @NotNull
    private Long endTime;

    private String agentId;

    private List<String> coinTypeList;
}
