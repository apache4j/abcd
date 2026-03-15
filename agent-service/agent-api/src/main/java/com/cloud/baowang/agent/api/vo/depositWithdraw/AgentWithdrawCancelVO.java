package com.cloud.baowang.agent.api.vo.depositWithdraw;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * @author qiqi
 */
@Data
@Schema(description = "代理出款取消VO")
public class AgentWithdrawCancelVO {

    @Schema(description="ID")
    @NotNull(message = "ID不能为空")
    private String id;

    @Schema(description="备注")
    @NotNull(message = "备注不能为空")
    private String payAuditRemark;

    @Schema(description="附件")
    @NotNull(message = "附件不能为空")
    private String fileKey;

    private String currentAdminId;


    private String currentAdminName;


}
