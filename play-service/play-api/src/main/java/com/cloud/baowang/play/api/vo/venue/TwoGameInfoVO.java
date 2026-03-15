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

import java.io.Serializable;

/**
 * @author qiqi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "游戏信息返回对象")
@I18nClass
public class TwoGameInfoVO implements Serializable {
    @Schema(description = "ID")
    private String id;

    @I18nField
    @Schema(description = "游戏名称")
    private String gameName;

    @Schema(description = "场馆CODE")
    private String venueCode;

    @Schema(description = "状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.PLATFORM_CLASS_STATUS_TYPE)
    private Integer status;

    @Schema(description = "多语言-状态名称")
    private String statusText;

    @Schema(description = "排序")
    private Integer sort;



}
