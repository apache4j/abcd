package com.cloud.baowang.agent.api.vo.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(title = "会员溢出审核请求")
public class MemberOverflowAuthReqVO {

    @Schema(description = "站点编码",hidden = true)
    private String siteCode;

    @Schema(description = "id")
    @NotNull(message = "主键不能为空")
    private String id;

    /**
     * {@link com.cloud.baowang.common.core.enums.ReviewStatusEnum}
     */
    @Schema(description = "审核状态(3-审核通过,4-审核拒绝) 同system_param review_status code值")
    @NotNull(message = "审核状态只能是3、4")
    @Min(value = 3, message = "审核状态只能是3、4")
    @Max(value = 4, message = "审核状态只能是3.4")
    private Integer auditStatus;

    @Schema(description = "审核备注")
    private String auditRemark;

}
