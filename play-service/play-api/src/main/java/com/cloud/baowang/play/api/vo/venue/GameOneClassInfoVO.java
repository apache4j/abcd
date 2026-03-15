package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author qiqi
 */
@Schema(description = "游戏平台VO对象")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@I18nClass
public class GameOneClassInfoVO implements Serializable {


    private String id;

    @Schema(description = "目录名称")
    private String directoryName;

    @Schema(description = "目录名称-多语言CODE", required = true)
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String directoryI18nCode;

    @Schema(description = "目录名称-多语言", required = true)
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private List<I18nMsgFrontVO> directoryI18nCodeList;


    @Schema(description = "一级分类-多语言图片", required = true)
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String typeIconI18nCode;

    @Schema(description = "一级分类-多语言", required = true)
    private List<I18nMsgFrontVO> typeIconI18nCodeList;

    @Schema(description = "首页名称")
    private String homeName;

    @Schema(description = "首页名称-多语言CODE", required = true)
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String homeI18nCode;

    @Schema(description = "首页名称-多语言")
    private List<I18nMsgFrontVO> homeI18nCodeList;

    @Schema(description = "状态 code:platform_class_status_type")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.PLATFORM_CLASS_STATUS_TYPE)
    private Integer status;

    @Schema(description = "状态名称")
    private String statusText;

    @Schema(description = "类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.GAME_ONE_TYPE)
    private Integer gameOneType;

    @Schema(description = "类型-名称")
    private String gameOneTypeText;

    @Schema(description = "图片CODE")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String icon;

    @Schema(description = "图片CODE")
    private String iconFileUrl;


    @Schema(description = "图片CODE2")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String icon2;

    @Schema(description = "图片CODE")
    private String icon2FileUrl;

    @Schema(description = "二级分类条数")
    private Integer twoClassSize;

    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "创建时间")
    private Long createdTime;

    @Schema(description = "更新人")
    private String updater;

    @Schema(description = "更新时间")
    private Long updatedTime;

    @Schema(description = "首页排序")
    private Integer homeSort;

    @Schema(description = "目录排序")
    private Integer directorySort;

    @Schema(description = "模板")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.GAME_MODEL)
    private String model;

    @Schema(description = "状态 code:game_model")
    private String modelText;

    @Schema(description = "最高返水")
    private BigDecimal maxRebateAmount;

    @Schema(description = "场馆对象")
    private List<GameOneClassVenueCurrencyVO> gameOneClassVenueInfo;

    @Schema(description = "游戏大厅显示-场馆侧边栏")
    private List<GameOneClassVenueInfoVO> lobbySignVenueCode;

    @Schema(description = "皮肤4:国内盘字段:奖金池")
    private BigDecimal prizePoolTotal;

    @Schema(description = "皮肤4:国内盘字段:奖金池开始金额")
    private BigDecimal prizePoolStart;

    @Schema(description = "皮肤4:国内盘字段:奖金池结束金额")
    private BigDecimal prizePoolEnd;

    @Schema(description = "皮肤4:国内盘字段:返水场馆类型标签")
    private Integer rebateVenueType;

    @Schema(description = "皮肤4:国内盘字段:场馆的币种返水")
    private Map<String, BigDecimal> currencyRebateMap;

}
