package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author qiqi
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "游戏平台修改请求对象")
public class SiteVenueInfoUpVO {

    @Schema(description = "游戏平台ID", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String id;

    @Schema(description = "场馆CODE", required = true)
    private String venueCode;

    @Schema(description = "冠名标签")
    private Integer siteLabelChangeType;

    @Schema(description = "PC-图标-多语言", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> pcIconI18nCodeList;

    @Schema(description = "H5-图标-多语言", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> h5IconI18nCodeList;

    @Schema(description = "场馆单币种商户配置", required = true)
    private List<VenueInfoCurrencyVO> venueInfoCurrencyList;


    @Schema(description = "背景图", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> pcBackgroundCodeList;

    @Schema(description = "图标", required = true)
    private List<I18nMsgFrontVO> pcLogoCodeList;

    @Schema(description = "场馆多语言", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> venueNameI18nCodeList;

    @Schema(description = "场馆描述", required = true)
    private List<I18nMsgFrontVO> venueDescI18nCodeList;

    @Schema(description = "小图标1-多语言")
    private List<I18nMsgFrontVO> smallIcon1I18nCodeList;

    @Schema(description = "小图标2-多语言")
    private List<I18nMsgFrontVO> smallIcon2I18nCodeList;

    @Schema(description = "小图标3-多语言")
    private List<I18nMsgFrontVO> smallIcon3I18nCodeList;

    @Schema(description = "小图标4-多语言")
    private List<I18nMsgFrontVO> smallIcon4I18nCodeList;

    @Schema(description = "小图标5-多语言")
    private List<I18nMsgFrontVO> smallIcon5I18nCodeList;

    @Schema(description = "小图标6-多语言")
    private List<I18nMsgFrontVO> smallIcon6I18nCodeList;

    @Schema(description = "游戏横版图标-多语言")
    private List<I18nMsgFrontVO> htIconI18nCodeList;


    @Schema(description = "中等图-多语言", required = true)
    private List<I18nMsgFrontVO> middleIconI18nCodeList;



}
