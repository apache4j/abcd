package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Schema(description = "一级分类悬浮模版")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@I18nClass
public class GameOneFloatConfigByGameOneIdVO implements Serializable {

    @Schema(description = "二级分类")
    private String gameTwoId;

    @Schema(description = "场馆")
    private String venueCode;

    @Schema(description = "悬浮名称")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String floatNameI18nCode;

    @Schema(description = "悬浮名称,多语言")
    private List<I18nMsgFrontVO> floatNameI18nCodeList;

    @Schema(description = "场馆名称-多语言")
    private List<I18nMsgFrontVO> venueNameList;

    @Schema(title = "品牌图标-多语言-悬浮图标")
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String logoIconI18nCode;

    @Schema(description = "品牌图标-多语言-悬浮图标")
    private List<I18nMsgFrontVO> logoIconI18nCodeList;

    @Schema(title = "中图标-多语言-悬浮图片")
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String mediumIconI18nCode;

    @Schema(description = "中图标-多语言-悬浮图片")
    private List<I18nMsgFrontVO> mediumIconI18nCodeList;




}
