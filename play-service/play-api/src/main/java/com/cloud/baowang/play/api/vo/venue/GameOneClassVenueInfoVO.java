package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@I18nClass
@Schema(description = "一级分类场馆详情")
public class GameOneClassVenueInfoVO {

    @Schema(description = "venueCode")
    private String venueCode;

    @Schema(description = "场馆名称")
    private String venueName;

    @Schema(description = "场馆类型")
    private Integer venueType;

    @Schema(description = "状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.PLATFORM_CLASS_STATUS_TYPE)
    private Integer status;

    @Schema(description = "状态名称")
    private String statusText;

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "场馆描述")
    private String venueDesc;


}
