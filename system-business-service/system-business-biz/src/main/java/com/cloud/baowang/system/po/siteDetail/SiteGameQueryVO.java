package com.cloud.baowang.system.po.siteDetail;

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

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@I18nClass
@Schema(description = "站点游戏查询视图")
public class SiteGameQueryVO implements Serializable {
    @Schema(description = "id")
    private String id;

    @Schema(description = "所属场馆Code")
    private String venueCode;

    @Schema(description = "游戏id")
    private String gameId;

    @Schema(description = "游戏名称")
    @I18nField
    private String gameI18nCode;


    @Schema(description = "状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_STATUS)
    private Integer status;
    @Schema(description = "状态中文")
    private String statusText;


}
