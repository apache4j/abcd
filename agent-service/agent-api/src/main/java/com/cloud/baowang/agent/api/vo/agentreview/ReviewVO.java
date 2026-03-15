package com.cloud.baowang.agent.api.vo.agentreview;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;


/**
 * @author: kimi
 */
@Data
@Schema(description ="审核结果 Request")
public class ReviewVO {

    @Schema(description = "id")
    @NotEmpty(message = "id不能为空")
    private String id;

    @Schema(description = "站点编码",hidden = true)
    private String siteCode;

    @Schema(description = "提交审核信息")
    private String reviewRemark;
}
