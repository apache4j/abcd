package com.cloud.baowang.system.api.vo.site.area;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@I18nClass
@Schema(description = "站点手机区号下拉框返回对象")
public class AreaSiteLangVO {
    @Schema(description = "区号")
    private String areaCode;
    @Schema(description = "国家名称")
    private String countryName;
    @Schema(description = "国家简写")
    private String countryCode;
    @Schema(description = "图标地址")
    private String icon;
    @Schema(title = "最大长度")
    private Integer maxLength;
    @Schema(title = "最小长度")
    private Integer minLength;
}
