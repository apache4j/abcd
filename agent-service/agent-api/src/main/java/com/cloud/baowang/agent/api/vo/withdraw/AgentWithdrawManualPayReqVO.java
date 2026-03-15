package com.cloud.baowang.agent.api.vo.withdraw;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "代理人工出款确认请求VO")
public class AgentWithdrawManualPayReqVO {

    @Schema(description = "id")
    private String id;

    @Schema(description = "出款凭证文件")
    private String fileKey;

    @Schema(description = "状态",hidden = true)
    private String customerStatus;
}
