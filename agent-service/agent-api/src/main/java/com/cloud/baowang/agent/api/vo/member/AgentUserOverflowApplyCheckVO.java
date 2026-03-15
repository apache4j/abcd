package com.cloud.baowang.agent.api.vo.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(title = "下级会员校验会员请求参数")
public class AgentUserOverflowApplyCheckVO {

    @NotBlank
    @Schema(description = "溢出会员")
    private String memberName;

    @Schema(description = "申请人", hidden = true)
    private String applyName;
}
