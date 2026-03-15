package com.cloud.baowang.user.api.vo.vip;

import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author 小智
 * @Date 4/5/23 11:14 AM
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "站点VIP权益配置入参对象")
public class SiteVIPBenefitRequestVO implements Serializable {

    @Schema(description = "站点code")
    private String siteCode;

    @Schema(description = "vip等级code")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer vipGradeCode;

    /* vip等级 */
    @Schema(description =  "vip等级")
    private String vipGrade;

    /* 周流水活动参与标识 */
    @Schema(description =  "周流水活动参与标识(0:不参与,1:参与)")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer weekFlag;

    /* 每周返还奖金比例(%) */
    @Schema(description =  "每周返还奖金比例(%)")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private BigDecimal weekRebate;

    /* 周奖励最低流水 */
    @Schema(description =  "周奖励最低流水")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private BigDecimal weekMinBetAmount;

    @Schema(description =  "周流水倍数")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private BigDecimal weekBetMultiple;

    /* 月流水活动参与标识 */
    @Schema(description =  "月流水活动参与标识(0:不参与,1:参与)")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer monthFlag;

    /* 月奖励最低流水 */
    @Schema(description =  "月奖励最低流水")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private BigDecimal monthMinBetAmount;

    /* 每月返还奖金比例(%) */
    @Schema(description =  "每月返还奖金比例(%)")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private BigDecimal monthRebate;

    /* 月流水倍数 */
    @Schema(description =  "月流水倍数")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private BigDecimal monthBetMultiple;

    /* 周体育流水活动参与标识 */
    @Schema(description =  "周体育流水活动参与标识(0:不参与,1:参与)")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer weekSportFlag;

    /* 月流水倍数 */
    @Schema(description =  "周体育最低流水")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private BigDecimal weekSportMinBet;

    /* 周体育倍数 */
    @Schema(description =  "周体育倍数")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private BigDecimal weekSportMultiple;

    /* 周体育奖金 */
    @Schema(description =  "周体育奖金")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private BigDecimal weekSportRebate;

    /* 升级奖金 */
    @Schema(description =  "升级奖金")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private BigDecimal upgrade;

    /* 转盘次数 */
    @Schema(description =  "转盘次数")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private BigDecimal luckTime;

    private String operator;
}
