package com.cloud.baowang.system.api.vo.site;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@I18nClass
@Schema(title = "站点底部栏等基础返回对象")
public class ContactInfoVO {
    @Schema(title = "平台名称")
    private String IMPlatformName;

    @Schema(title = "帐号")
    private String account;
}
