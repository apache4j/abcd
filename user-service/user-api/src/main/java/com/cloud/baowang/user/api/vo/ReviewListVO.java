package com.cloud.baowang.user.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
@Schema(title= "审核结果 Request")
public class ReviewListVO{
    @Schema(title = "id")
    @NotEmpty(message = "id不能为空")
    private List<String> id;

    @Schema(title = "提交审核信息")
    private String reviewRemark;

    @Schema(title = "站点code",hidden = true)
    private String siteCode;
}
