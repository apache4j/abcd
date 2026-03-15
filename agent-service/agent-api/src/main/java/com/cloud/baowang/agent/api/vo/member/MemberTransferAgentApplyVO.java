package com.cloud.baowang.agent.api.vo.member;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(title ="会员转代申请")
public class MemberTransferAgentApplyVO {
    @Schema(description = "siteCode",hidden = true)
    private String siteCode;

    @NotBlank
    @Schema(description ="转代会员ID")
    private String userAccount;

    //@NotBlank
    @Schema(description ="转代会员注册信息")
    private String userRegister;

    @NotBlank
    @Schema(description ="转入代理")
    private String transferAgentName;

    @NotBlank
    @Schema(title ="申请备注信息")
    private String applyRemark;
}
