package com.cloud.baowang.agent.api.vo.agentreview.info;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "代理信息修改审核入参")
public class AgentInfoModifyReviewVO {
    @NotNull
    @Schema(description = "id")
    private Long id;

    @Schema(description = "操作人", hidden = true)
    private String operator;
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;
    /**
     * {@link com.cloud.baowang.common.core.enums.ReviewStatusEnum}
     */
    @Max(value = 4, message = "审核状态错误")
    @Min(value = 3, message = "审核状态错误")
    @NotNull
    @Schema(description = "审核状态 3 通过 4 拒绝")
    private Integer status;

    @Length(max = 50)
    @Schema(description = "备注")
    private String remark;

}
