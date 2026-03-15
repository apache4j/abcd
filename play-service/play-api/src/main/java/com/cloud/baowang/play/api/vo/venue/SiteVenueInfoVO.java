package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
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
@I18nClass
@Schema(description = "站点游戏平台VO对象")
public class SiteVenueInfoVO {

    @Schema(description = "ID")
    private String id;

    @Schema(description = "场馆类型 1:体育,2:视讯,3:棋牌,4:电子,5:彩票,6:斗鸡,7:电竞")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VENUE_TYPE)
    private Integer venueType;

    @Schema(description = "场馆类型名称")
    private String venueTypeText;

    @Schema(description = "游戏平台名称")
    @I18nField
    private String venueName;

    @Schema(description = "币种列表")
    private List<String> currencyCodeList;

    @Schema(description = "币种")
    private String currencyCode;


    @Schema(description = "平台币种名称")
    private String venueCurrencyCode;

    @Schema(description = "游戏平台Code")
    private String venueCode;

    @Schema(description = "状态名称,字典code:platform_class_status_type")
    private String statusText;

    @Schema(description = "状态（ 1开启中 2 维护中 3 已禁用)")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.PLATFORM_CLASS_STATUS_TYPE)
    private Integer status;

    @Schema(description = "三方平台")
    private String venuePlatform;

    @Schema(description = "游戏平台名称")
    private String venuePlatformName;

    @Schema(description = "默认中文图片")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String venueIcon;

    @Schema(description = "默认中文图片")
    private String venueIconFileUrl;

    @Schema(description = "PC-图片-多语言", required = true)
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String pcIconI18nCode;

    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> pcIconI18nCodeList;

    @Schema(description = "H5图片-多语言", required = true)
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String h5IconI18nCode;

    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> h5IconI18nCodeList;


    @Schema(description = "背景图-多语言", required = true)
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String pcBackgroundCode;

    private List<I18nMsgFrontVO> pcBackgroundCodeList;

    @Schema(description = "场馆图标-多语言", required = true)
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String pcLogoCode;

    private List<I18nMsgFrontVO> pcLogoCodeList;

    @Schema(description = "场馆名称-多语言", required = true)
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String venueNameI18nCode;

    private List<I18nMsgFrontVO> venueNameI18nCodeList;


    @Schema(description = "最近操作人")
    private String updater;

    @Schema(description = "场馆负盈利费率")
    private BigDecimal venueProportion;

    @Schema(description = "场馆费率类型 字典CODE:venue_proportion_type")
    private Integer proportionType;

    @Schema(description = "场馆有效流水费率")
    private BigDecimal validProportion;

    @Schema(description = "维护开始时间")
    private Long maintenanceStartTime;

    @Schema(description = "维护结束时间")
    private Long maintenanceEndTime;

    @Schema(description = "更新时间")
    private Long updatedTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(title = "创建人")
    private String creator;


    @Schema(title = "多币种场馆")
    private List<VenueInfoCurrencyVO> venueInfoCurrencyList;

    @Schema(title = "单币种场馆单最后一个币种,用来给前端高亮显示")
    private String lastCurrencyCode;

    @Schema(description = "场馆接入类型:1:数据源,2:场馆,3:游戏")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VENUE_JOIN_TYPE)
    private Integer venueJoinType;

    @Schema(title = "场馆接入类型:1:数据源,2:场馆,3:游戏")
    private String venueJoinTypeText;

    @Schema(title = "描述")
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String venueDescI18nCode;


    private List<I18nMsgFrontVO> venueDescI18nCodeList;

    @Schema(title = "小图标1")
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String smallIcon1I18nCode;

    @Schema(description = "小图标1-多语言")
    private List<I18nMsgFrontVO> smallIcon1I18nCodeList;

    @Schema(title = "小图标2")
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String smallIcon2I18nCode;

    @Schema(description = "小图标2-多语言")
    private List<I18nMsgFrontVO> smallIcon2I18nCodeList;

    @Schema(title = "小图标3")
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String smallIcon3I18nCode;

    @Schema(description = "小图标4-多语言")
    private List<I18nMsgFrontVO> smallIcon3I18nCodeList;

    @Schema(title = "小图标4")
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String smallIcon4I18nCode;

    @Schema(description = "小图标4-多语言")
    private List<I18nMsgFrontVO> smallIcon4I18nCodeList;

    @Schema(title = "小图标5")
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String smallIcon5I18nCode;

    @Schema(description = "小图标5-多语言")
    private List<I18nMsgFrontVO> smallIcon5I18nCodeList;

    @Schema(title = "小图标6")
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String smallIcon6I18nCode;

    @Schema(description = "小图标6-多语言")
    private List<I18nMsgFrontVO> smallIcon6I18nCodeList;

    @Schema(title = "游戏横版图标")
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String htIconI18nCode;

    @Schema(description = "游戏横版图标-多语言")
    private List<I18nMsgFrontVO> htIconI18nCodeList;
}
