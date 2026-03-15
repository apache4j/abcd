package com.cloud.baowang.system.api.vo.site.agreement;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户协议编辑VO")
public class UserAgreementEditVO {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;
    @Schema(description = "语言")
    private String language;
    @Schema(description = "协议")
    private String agreement;
    @Schema(description = "updater", hidden = true)
    private String updater;
}
