package com.cloud.baowang.user.api.vo.vip;

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
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author 小智
 * @Date 4/5/23 2:09 PM
 * @Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "站点后台VIP段位配置返回对象")
@I18nClass
public class SiteVIPBenefitVO implements Serializable {

    @Schema(description = "颜色")
    private String rankColor;

    @Schema(description = "vip段位code")
    private Integer vipRankCode;

    @Schema(description = "vip段位图片")
    private String vipIconImage;

    @Schema(description = "vip段位名称i18Code")
    @I18nField
    private String vipRankNameI18nCode;

    /* vip段位对应等级最小值 */
    @Schema(description = "vip段位对应等级最小值")
    private Integer minVipGrade;

    /* vip段位对应等级最小值 */
    @Schema(description = "vip段位对应等级最小值名称")
    private String minVipGradeName;

    /* vip段位对应等级最小值 */
    @Schema(description = "vip段位对应等级最大值")
    private Integer maxVipGrade;

    /* vip段位对应等级最小值 */
    @Schema(description = "vip段位对应等级最大值名称")
    private String maxVipGradeName;

    @Schema(description = "升级奖金(0:没有,1:锁,2:未锁)")
    private Integer upgradeFlag;

    /* 升级奖金 */
    @Schema(description = "升级奖金")
    private BigDecimal upgrade;

    @Schema(description = "周流水奖金(0:没有,1:锁,2:未锁)")
    private Integer weekAmountFlag;

    @Schema(description = "周流水比例1")
    private BigDecimal weekAmountProp1;

    @Schema(description = "周流水比例2")
    private BigDecimal weekAmountProp2;

    @Schema(description = "月流水比例1")
    private BigDecimal monthAmountProp1;

    @Schema(description = "月流水比例2")
    private BigDecimal monthAmountProp2;

    @Schema(description = "月流水奖金(0:没有,1:锁,2:未锁)")
    private Integer monthAmountFlag;

    @Schema(description = "周体育奖金(0:没有,1:锁,2:未锁)")
    private Integer weekSportFlag;

    @Schema(description = "加密货币提款手续费(0:没有,1:锁,2:未锁)")
    private Integer encryCoinFee;

    @Schema(description = "周体育奖金明细")
    private List<SiteVIPWeekSportVO> vipWeekSportVOS;

    @Schema(description = "豪华礼品(0:没有,1:锁,2:未锁)")
    private Integer luxuriousGiftsFlag;

    @Schema(description = "VIP专属福利(0:没有,1:锁,2:未锁)")
    private Integer svipWelfareFlag;

    @Schema(description = "幸运转盘(0:没有,1:锁,2:未锁)")
    private Integer luckFlag;

    @Schema(description = "幸运转盘对应最小VIP等级")
    private String luckMinVipGradeName;

    @Schema(description = "加密货币免费提现对应最小VIP等级")
    private String encryMinVipGradeName;

    @Schema(description = "反水 (0:没有,1:锁,2:未锁)")
    private Integer rebateFlag;

}
