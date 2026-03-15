package com.cloud.baowang.user.api.vo.vip;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(title = "vip变更记录请求实体")
public class SiteVipChangeRecordRequestVO implements Serializable {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;

    @Schema(description = "会员id,预留")
    private String userId;

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "变更前vip等级")
    @NotBlank(message = ConstantsCode.PARAM_ERROR)
    private String beforeChange;

    @Schema(description = "变更后vip等级")
    @NotBlank(message = ConstantsCode.PARAM_ERROR)
    private String afterChange;

    @Schema(description = "变更时间")
    private Long changeTime;

    @Schema(description = "操作人")
    private String operator;
}
