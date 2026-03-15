package com.cloud.baowang.agent.api.vo.agentreview;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
@Schema(title= "审核结果 Request")
public class ReviewListVO {
    @Schema(title = "id")
    @NotEmpty(message = "id不能为空")
    private List<String> id;

    @Schema(title = "提交审核信息")
    private String reviewRemark;

    @Schema(title = "站点code",hidden = true)
    private String siteCode;

    @Schema(description = "操作人账号",hidden = true)
    private String operatorName;
}
