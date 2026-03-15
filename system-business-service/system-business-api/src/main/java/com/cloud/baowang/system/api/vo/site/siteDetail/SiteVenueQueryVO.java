package com.cloud.baowang.system.api.vo.site.siteDetail;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@I18nClass
@Schema(description = "站点场馆游戏视图对象")
public class SiteVenueQueryVO implements Serializable {
    @Schema(description = "id")
    private String id;

    @Schema(description = "场馆名称")
    private String venueName;

    @Schema(description = "场馆code")
    private String venueCode;

    @Schema(description = "场馆类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VENUE_TYPE)
    private Integer venueType;

    @Schema(description = "场馆类型中文")
    private String venueTypeText;

    @Schema(description = "状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_STATUS)
    private Integer status;

    @Schema(description = "状态")
    private String statusText;

    @Schema(description = "场馆对应游戏列表")
    private List<SiteGameQueryVO> siteGameQueryVOS;

}
