package com.cloud.baowang.agent.api.vo.withdrawConfig;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@I18nClass
@Schema(title = "代理提款设置 详情")
@Builder
public class AgentWithdrawDetailRspVO {

    @Schema(description = "币种多语言")
    @I18nField
    private String value;
    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "配置详情")
    private List<AgentWithdrawConfigDetailVO> detailList;
}
