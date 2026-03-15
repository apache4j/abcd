package com.cloud.baowang.agent.api.vo.withdraw;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
@Schema(title = "代理人工出款详情请求VO")
public class AgentWithdrawManualDetailReqVO {

    @Schema( description= "id")
    @NotNull(message = "id不能为空")
    private String id;

    /**
     * 数据脱敏 true 需要脱敏 false 不需要脱敏
     */
    @Schema(description = "数据脱敏 true 需要脱敏 false 不需要脱敏", hidden = true)
    private Boolean dataDesensitization;
}
