package com.cloud.baowang.agent.api.vo.merchant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "审核vo")
@Data
public class AuditVO {
    @Schema(description = "主键")
    private String id;
    @Schema(description = "审核备注")
    private String auditRemark;
    @Schema(description = "操作人",hidden = true)
    private String account;
}
