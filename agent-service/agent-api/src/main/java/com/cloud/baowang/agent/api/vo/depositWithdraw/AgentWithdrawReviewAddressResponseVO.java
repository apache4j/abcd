package com.cloud.baowang.agent.api.vo.depositWithdraw;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Schema(title = "代理地址详情列表返回对象")
@Data
public class AgentWithdrawReviewAddressResponseVO {



    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "首次使用时间")
    private Long firstUsedTime;

    @Schema(description = "最后使用时间")
    private Long lastUsedTime;


    @Schema(description = "总使用次数")
    private Integer usedNums;

    @Schema(description = "币种")
    private String currencyCode;

}
