package com.cloud.baowang.system.api.vo;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "查询邀请码-域名接口")
@Data
public class AgentInviteCodeDomainQueryVO {
    @Schema(description = "短链接")
    @NotBlank(message = ConstantsCode.PARAM_MISSING)
    private String shortUrl;
}
