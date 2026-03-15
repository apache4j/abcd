package com.cloud.baowang.agent.api.vo.manualup;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * @author: kimi
 */
@Data
@Schema(description = "代理人工扣除额度 请求VO")
public class AgentManualDownAddVO {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;

    @Schema(description = "代理账号调，金额集合")
    @NotEmpty(message = "代理信息不能为空")
    private List<AgentManualUpDownAccountVO> agentManualUpDownAccountVOS;


    @Schema(description = "钱包类型")
    @NotNull(message = "钱包类型不能为空")
    private Integer walletTypeCode;

    @Schema(description = "调整类型")
    @NotNull(message = "调整类型不能为空")
    private Integer adjustType;

    @Schema(description = "上传附件地址")
    private String certificateAddress;

    @Schema(description = "申请原因")
    @NotEmpty(message = "申请原因不能为空")
    private String applyReason;

}
