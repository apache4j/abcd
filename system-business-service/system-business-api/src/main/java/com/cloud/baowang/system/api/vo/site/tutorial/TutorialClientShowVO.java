package com.cloud.baowang.system.api.vo.site.tutorial;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@I18nClass
@Schema(title ="教程返回客户端vo")
public class TutorialClientShowVO {
    @Schema(title ="父id")
    private String id;
    @Schema(title ="父名称")
    @I18nField
    private String name;
    @Schema(title ="图标")
    private String icon;
    @Schema(title ="内容")
    @I18nField
    private String value;

    @Schema(title ="联系我们-投诉邮箱取value,客服邮箱用这个")
    private String valueExtends;

    @Schema(title ="排序code")
    private Integer code;

}
