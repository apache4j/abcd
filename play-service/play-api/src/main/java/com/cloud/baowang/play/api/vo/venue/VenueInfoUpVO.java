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
public class VenueInfoUpVO {

    @Schema(description = "游戏平台ID", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String id;

    @Schema(description = "场馆CODE", required = true)
    private String venueCode;

    @Schema(description = "场馆类型:场馆类型 1:体育,2:视讯,3:棋牌,4:电子,5:彩票,6:斗鸡,7:电竞 字典CODE:venue_type")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer venueType;

    @Schema(description = "币种")
    private List<String> currencyCodeList;

    @Schema(description = "币种")
    private String venueCurrencyCode;

    @Schema(description = "币种类型 字典CODE:venue_currency_type")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer venueCurrencyType;

    @Schema(description = "游戏平台名称")
    private String venuePlatformName;

    @Schema(description = "场馆费率类型 字典CODE:venue_proportion_type")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer proportionType;

    @Schema(description = "API URL", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String apiUrl;


    @Schema(description = "商户编码", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String merchantNo;

    @Schema(description = "AES 密钥", required = true)
    private String aesKey;


    @Schema(description = "商户密钥", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String merchantKey;


    @Schema(description = "场馆负盈利费率", required = true)
//    @DecimalMax(value = "100", message = ConstantsCode.PARAM_ERROR)
//    @DecimalMin(value = "0.01", message = ConstantsCode.PARAM_ERROR)
    private BigDecimal venueProportion;


    @Schema(description = "场馆有效流水费率", required = true)
//    @DecimalMax(value = "100", message = ConstantsCode.PARAM_ERROR)
//    @DecimalMin(value = "0.01", message = ConstantsCode.PARAM_ERROR)
    private BigDecimal validProportion;

    @Schema(description = "状态（ 1开启中 2 维护中 3 已禁用)")
    private Integer status;

    private String updater;

    @Schema(description = "维护时间开始")
    private Long maintenanceStartTime;

    @Schema(description = "维护时间结束")
    private Long maintenanceEndTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "PC-图标-多语言", required = true)
//    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> pcIconI18nCodeList;

    @Schema(description = "H5-图标-多语言", required = true)
//    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> h5IconI18nCodeList;

    @Schema(description = "场馆单币种商户配置", required = true)
    private List<VenueInfoCurrencyVO> venueInfoCurrencyList;


    @Schema(description = "背景图", required = true)
//    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
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
//    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> middleIconI18nCodeList;



}
