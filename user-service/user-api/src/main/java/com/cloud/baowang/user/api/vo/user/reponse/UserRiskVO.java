package com.cloud.baowang.user.api.vo.user.reponse;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "会员风控层级下拉 返回")
public class UserRiskVO {
    @Schema(title =  "风控层级id")
    private String riskLevelId;
    @Schema(title =  "风控层级")
    private String riskLevel;
}
