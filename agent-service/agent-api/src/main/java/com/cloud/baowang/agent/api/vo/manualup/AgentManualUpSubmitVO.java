package com.cloud.baowang.agent.api.vo.manualup;

import com.cloud.baowang.agent.api.enums.AgentManualAdjustTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: kimi
 */
@Data
@Schema(description = "代理人工添加额度申请-提交 Request")
public class AgentManualUpSubmitVO {

    @Schema(description = "代理账号调，金额集合")
    @NotEmpty(message = "代理信息不能为空")
    private List<AgentManualUpDownAccountVO> agentManualUpDownAccountVOS;

    @Schema(description = "平台币代码(代理发起人工加减额都是当前站点的平台币)", hidden = true)
    private String currencyCode;
    @Schema(description = "钱包类型")
    @NotNull(message = "钱包类型不能为空")
    private Integer walletTypeCode;
    /**
     * system_param agent_manual_adjust_type
     * {@link AgentManualAdjustTypeEnum}
     */
    @Schema(description = "调整类型")
    @NotNull(message = "调整类型不能为空")
    private Integer adjustType;

    @Schema(description = "上传附件地址")
    private String certificateAddress;

    @Schema(description = "流水倍数")
    private BigDecimal runningWaterMultiple;

    @Schema(description = "申请原因")
    @NotEmpty(message = "申请原因不能为空")
    private String applyReason;

    @Schema(description = "站点code", hidden = true)
    private String siteCode;
}
