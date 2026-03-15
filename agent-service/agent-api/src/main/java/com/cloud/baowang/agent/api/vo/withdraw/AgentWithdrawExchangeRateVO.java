package com.cloud.baowang.agent.api.vo.withdraw;


import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(title = "代理提款汇率返回")
public class AgentWithdrawExchangeRateVO {

    @Schema(description = "主货币汇率")
    private BigDecimal exchangeRate;

    @Schema(description = "平台币汇率")
    private BigDecimal platformExchangeRate;
}
