package com.cloud.baowang.user.api.vo.vip;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author : 小智
 * @Date : 2024/8/6 17:59
 * @Version : 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "VIP段位配置编辑对象")
@Valid
public class VIPRankUpdateVO {

    @Schema(description = "VIP段位名称-中文名")
    private String vipRankName;

    @Schema(description = "多语言-VIP段位名称")
    private List<I18nMsgFrontVO> vipRankNameI18nCodeList;

    @Schema(description = "VIP段位code")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer vipRankCode;

    @Schema(description = "VIP等级数组")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private List<Long> vipGradeCode;

    @Schema(description = "段位币种配置信息列表")
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    @Valid
    private List<SiteVipRankCurrencyConfigVO> currencyConfigVOS;

    @Schema(description = "VIP段位说明")
    private String remark;

    @Schema(description = "VIP段位图标")
    private String vipIcon;

    @Schema(description = "颜色")
    private String rankColor;
    /**
     * {@link com.cloud.baowang.common.core.enums.YesOrNoEnum}
     * 转盘是否参与(0:不参与,1:参与)
     */
    @Schema(description = "转盘是否参与(0:不参与,1:参与)")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer luckFlag;

    /* 转盘次数 */
    @Schema(description = "转盘次数")
    private BigDecimal luck;

    /**
     * {@link com.cloud.baowang.common.core.enums.YesOrNoEnum}
     * 周流水奖励是否参加
     */
    @Schema(description = "周流水奖励是否参加(0.不参与，1.参与)")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer weekAmountFlag;

    /* 周流水奖励达成条件 */
    @Schema(description = "周流水奖励达成条件")
    private BigDecimal weekAmountLimit;

    /* 周流水奖励比例1 */
    @Schema(description = "周流水奖励比例1")
    private BigDecimal weekAmountProp1;

    /* 周流水奖励比例2 */
    @Schema(description = "周流水奖励比例2")
    private BigDecimal weekAmountProp2;

    /* 周流水倍数 */
    @Schema(description = "周流水倍数")
    private BigDecimal weekAmountMultiple;

    /**
     * {@link com.cloud.baowang.common.core.enums.YesOrNoEnum}
     * 月流水奖励是否参加
     */
    @Schema(description = "月流水奖励是否参加（0.不参与，1.参与）")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer monthAmountFlag;

    /* 月流水奖励达成条件 */
    @Schema(description = "月流水奖励达成条件")
    private BigDecimal monthAmountLimit;

    /* 月流水奖励比例1 */
    @Schema(description = "月流水奖励比例1")
    private BigDecimal monthAmountProp1;

    /* 月流水奖励比例2 */
    @Schema(description = "月流水奖励比例2")
    private BigDecimal monthAmountProp2;

    /* 月流水倍数 */
    @Schema(description = "月流水倍数")
    private BigDecimal monthAmountMultiple;
    /**
     * {@link com.cloud.baowang.common.core.enums.YesOrNoEnum}
     * 是否参加周体育奖励
     */
    @Schema(description = "是否参加周体育奖励（0.不参与，1.参与）")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer weekSportFlag;

    @Schema(description = "周体育内容数组")
    private List<SiteVipSportReqVo> sportReqVos;
    /**
     * {@link com.cloud.baowang.common.core.enums.YesOrNoEnum}
     * 是否有加密货币提款手续费
     */
    @Schema(description = "是否有加密货币提款手续费")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer encryCoinFee;

    /* 是否有SVIP专属福利 */
    @Schema(description = "是否有SVIP专属福利")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer svipWelfare;

    /* 是否有豪华赠品 */
    @Schema(description = "是否有豪华赠品")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer luxuriousGifts;

    /**是否显示反水特权配置*/
    @Schema(description = "是否显示反水特权配置")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer rebateConfig;

}
