package com.cloud.baowang.site.vo;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@I18nClass
public class ActivityVO implements Serializable {

    @Schema(title = "活动id")
    private String activityId;

    @Schema(title = "活动名称")
    @I18nField
    private String activityNameI18nCode;

}
