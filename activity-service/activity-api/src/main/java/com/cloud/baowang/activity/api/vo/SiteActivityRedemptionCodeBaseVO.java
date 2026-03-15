package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author brence
 * @desc 兑换码基础信息VO
 * @date 2025-10-27
 */
@Data
@Builder
@AllArgsConstructor
public class SiteActivityRedemptionCodeBaseVO implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 兑换码基础信息id，采用雪花算法生成的id
     */

    @Schema(description = "兑换码基础信息id，采用雪花算法生成的id")
    private Long id;

    /**
     * 兑换码订单号
     */
    @Schema(description = "兑换码订单号")
    @NotNull(message = "兑换码订单号不能为空")
    private String orderNo;

    /**
     * 兑换码类型，0:通用兑换码，1:唯一兑换码
     */
    @Schema(description = "兑换码类型，0:通用兑换码，1:唯一兑换码")
    private Integer category;

    /**
     * 平台币或法币：0:平台币，1:法币
     */
    @Schema(description = "平台币或法币：0:平台币，1:法币")
    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.ENABLE_DISABLE_TYPE)
    private String platformOrFiatCurrency;

    /**
     * 活动规则，多语言
     */
    @Schema(description = "活动规则，多语言")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String activityRuleI18nCode;
    /**
     * 活动规则，多语言
     */
    @Schema(description = "活动规则，多语言")
    private List<I18nMsgFrontVO> activityRuleI18nCodeList;

    /**
     * 活动规则描述，多语言
     */
    @Schema(description = "活动规则描述，多语言")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String activityRuleDescI18nCode;

    @Schema(description = "活动规则描述，多语言")
    private List<I18nMsgFrontVO> activityRuleDescI18nCodeList;

    /**
     * app端活动头图
     */
    @Schema(description = "app端活动头图，多语言")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String headPictureAppI18nCode;

    @Schema(description = "app端活动头图，多语言")
    private List<I18nMsgFrontVO> headPictureAppI18nCodeList;

    /**
     * PC端活动头图
     */
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    @Schema(description = "PC端活动头图")
    private String headPicturePcI18nCode;

    @Schema(description = "PC端活动头图")
    private List<I18nMsgFrontVO> headPicturePcI18nCodeList;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Long createdTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private Long updatedTime;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String creator;

    /**
     * 修改人
     */
    @Schema(description = "修改人")
    private String updater;
    /**
     * 客户端开关，0:关闭，1:开启
     */
    @Schema(title = "客户端开关，0:关闭，1:开启")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_TYPE)
    private Integer clientSwitch;

    @Schema(title = "客户端开关，0:关闭，1:开启")
    private String clientSwitchText;



    /**
     * 活动时效类型，0:限时，1:长期
     */
    @Schema(title = "活动时效 0-限时，1-长期")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ACTIVITY_DEADLINE)
    private Integer deadlineType;

    @Schema(title = "活动时效 0-限时，1-长期")
    private String deadlineTypeText;
    /**
     * 站点编码
     */
    @Schema(description = "站点编码")
    private String siteCode;

    /**
     * 状态：0:禁用，1:正常
     */
    @Schema(description = "状态：0:禁用，1:正常")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_TYPE)
    private Integer status;
    /**
     * 状态：0:禁用，1:正常
     */
    @Schema(description = "状态：0:禁用，1:正常")
    private String statusText;

}
