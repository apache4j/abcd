package com.cloud.baowang.agent.api.vo.depositWithdraw;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
@Schema(title = "提款审核详情请求VO")
public class AgentWithdrawReviewDetailReqVO {

    @Schema( description= "id")
    @NotNull(message = "id不能为空")
    private Long id;

    /**
     * 数据脱敏 true 需要脱敏 false 不需要脱敏
     */
    private Boolean dataDesensitization;
}
