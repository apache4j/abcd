package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author qiqi
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "游戏一级分类悬浮配置-新增")
public class GameOneFloatConfigDetailAddReqVO {

    @Schema(description = "悬浮名称多语言CODE")
    private String floatNameI18nCode;

    @Schema(description = "二级分类ID")
    private String gameTwoId;

    @Schema(description = "场馆CODE")
    private String venueCode;

    @Schema(description = "悬浮名称,多语言 字典code:language_type")
    private List<I18nMsgFrontVO> floatNameI18nCodeList;

    @Schema(description = "品牌图标-多语言 字典code:language_type", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> logoIconI18nCodeList;

    @Schema(description = "中图标-多语言 字典code:language_type", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> mediumIconI18nCodeList;



}
