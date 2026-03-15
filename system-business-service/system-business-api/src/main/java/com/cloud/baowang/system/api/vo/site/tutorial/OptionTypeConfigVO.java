package com.cloud.baowang.system.api.vo.site.tutorial;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.system.api.api.i18n.dto.I18NMessageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@I18nClass
@Schema(title = "信息配置管理返回VO")
public class OptionTypeConfigVO {

    private String id;

    @Schema(title = "排序code")
    private Integer code;
    @Schema(title = "配置选项")
    @I18nField
    private String optionType;

    @Schema(title = "最近编辑人")
    private String updater;

    @Schema(title = "最近编辑时间")
    private Long updatedTime;


}
