package com.cloud.baowang.user.api.vo.vip;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


/**
 * @Author : 小智
 * @Date : 2024/8/2 15:22
 * @Version : 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "VIP段位返回对象")
@I18nClass
public class SiteVIPRankVO implements Serializable {
    @Schema(description = "主键")
    private String id;

    @Schema(description = "siteCode")
    private String siteCode;

    @Schema(description = "VIP段位code")
    private Integer vipRankCode;
    @Schema(description = "段位颜色")
    private String rankColor;

    @Schema(description = "vip等级codes")
    private String vipGradeCodes;

    @Schema(description = "vip等级默认中文名")
    private String vipGradeCodesName;

    @Schema(description = "VIP等级列表")
    private List<Integer> vipGradeList;

    @Schema(description = "VIP等级详情列表")
    private List<SiteVIPGradeVO> vipGradeVoList;

    @Schema(description = "VIP等级对应最小等级")
    private Integer minVipGrade;
    @Schema(description = "VIP等级对应最小等级名称")
    private String minVipGradeName;

    @Schema(description = "VIP等级对应最大等级")
    private Integer maxVipGrade;

    @Schema(description = "VIP等级对应最大等级名称")
    private String maxVipGradeName;

    @Schema(description = "VIP段位名称")
    private String vipRankName;

    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    @Schema(description = "vip段位名称i18Code")
    private String vipRankNameI18nCode;

    @Schema(description = "币种配置信息列表")
    private List<SiteVipRankCurrencyConfigVO> currencyConfigVOS;

    @Schema(description = "多语言list")
    private List<I18nMsgFrontVO> vipRankNameI18nCodeList;

    @Schema(description = "VIP段位图标")
    private String vipIcon;
    @Schema(description = "vip段位图标-展示用")
    private String vipIconImage;

    @Schema(description = "VIP段位备注")
    private String remark;
    /**
     * {@link com.cloud.baowang.common.core.enums.YesOrNoEnum}
     */
    @Schema(description = "转盘是否参与(0:不参与,1:参与)")
    private Integer luckFlag;

    @Schema(description = "是否允许当前条件勾选")
    private Integer luckFlagIsShow;

    @Schema(description = "转盘次数")
    private BigDecimal luck;

    /**
     * {@link com.cloud.baowang.common.core.enums.YesOrNoEnum}
     */
    @Schema(description = "周流水奖励是否参加")
    private Integer weekAmountFlag;

    @Schema(description = "是否允许当前条件勾选")
    private Integer weekAmountFlagIsShow;

    @Schema(description = "周流水奖励达成条件")
    private BigDecimal weekAmountLimit;

    @Schema(description = "周流水奖励比例1")
    private BigDecimal weekAmountProp1;

    @Schema(description = "周流水奖励比例2")
    private BigDecimal weekAmountProp2;

    @Schema(description = "周流水倍数")
    private BigDecimal weekAmountMultiple;

    /**
     * {@link com.cloud.baowang.common.core.enums.YesOrNoEnum}
     */
    @Schema(description = "月流水奖励是否参加")
    private Integer monthAmountFlag;

    @Schema(description = "是否允许当前条件勾选")
    private Integer monthAmountFlagIsShow;

    @Schema(description = "月流水奖励达成条件")
    private BigDecimal monthAmountLimit;

    @Schema(description = "月流水奖励比例1")
    private BigDecimal monthAmountProp1;

    @Schema(description = "月流水奖励比例2")
    private BigDecimal monthAmountProp2;

    @Schema(description = "月流水倍数")
    private BigDecimal monthAmountMultiple;
    /**
     * {@link com.cloud.baowang.common.core.enums.YesOrNoEnum}
     */
    @Schema(description = "是否参加周体育奖励")
    private Integer weekSportFlag;

    @Schema(description = "当前条件是否允许勾选")
    private Integer weekSportFlagIsShow;

    @Schema(description = "周体育流水礼金列表（如果有）")
    private List<SiteVipSportVo> sportVos;

    /**
     * {@link com.cloud.baowang.common.core.enums.YesOrNoEnum}
     */
    @Schema(description = "是否有加密货币提款手续费")
    private Integer encryCoinFee;

    @Schema(description = "当前条件是否允许勾选")
    private Integer encryCoinFeeIsShow;

    @Schema(description = "是否有SVIP专属福利")
    private Integer svipWelfare;
    @Schema(description = "当前条件是否允许勾选")
    private Integer svipWelfareIsShow;
    @Schema(description = "是否有豪华赠品")
    private Integer luxuriousGifts;
    @Schema(description = "当前条件是否允许勾选")
    private Integer luxuriousGiftsIsShow;

    @Schema(description = "是否有显示反水特权配置")
    private Integer rebateConfig;
    @Schema(description = "当前条件是否允许勾选显示反水特权配置")
    private Integer rebateConfigIsShow;


}
