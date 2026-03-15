package com.cloud.baowang.wallet.api.vo.site;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


/**
 * @author: kimi
 */
@Data
@Schema(description ="审核结果 Request")
public class ReviewMssagVO {

    @Schema(description = "id")
    @NotEmpty(message = "id不能为空")
    private String id;

    @Schema(description = "提交审核信息")
    private String reviewRemark;
}
