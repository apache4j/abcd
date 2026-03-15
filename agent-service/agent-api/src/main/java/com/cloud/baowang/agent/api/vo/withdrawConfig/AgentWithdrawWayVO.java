package com.cloud.baowang.agent.api.vo.withdrawConfig;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 */
@Data
@Schema(title = "客户端 代理提款配置返回信息对象")
@I18nClass
public class AgentWithdrawWayVO {

    @Schema(description = "站点")
    private String siteCode;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "提款方式-固定中文")
    private String withdrawWay;

    @Schema(description = "提款方式- i18")
    @I18nField
    private String withdrawWayI18;

    @Schema(description = "方式id")
    private String withdrawWayId;

}
