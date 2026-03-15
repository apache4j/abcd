package com.cloud.baowang.user.api.vo.vip;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author 小智
 * @Date 4/5/23 2:09 PM
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "VIP权益配置返回对象")
public class VIPGradeBenefitVO implements Serializable {

    @Schema(title = "id")
    private Long id;

    @Schema(title = "vip等级code")
    private Integer vipRankCode;

    /* vip等级 */
    @Schema(title = "vip等级")
    private String vipRank;

    /* 日提款次数 */
    @Schema(title = "日提款次数")
    private Integer dailyWithdrawals;

    /* 每日累计提款额度 */
    @Schema(title = "每日累计提款额度")
    private BigDecimal dayWithdrawLimit;

    /* 升级礼金 */
    @Schema(title = "升级礼金")
    private BigDecimal upgradeBonus;

    /* 生日礼金 */
    @Schema(title = "生日礼金")
    private BigDecimal birthdayBonus;

    /* 上半月礼金 */
    @Schema(title = "上半月礼金")
    private BigDecimal firstHalfMonthBonus;

    /* 下半月礼金 */
    @Schema(title = "下半月礼金")
    private BigDecimal secondHalfMonthBonus;

    /* 半月累计存款 */
    @Schema(title = "半月累计存款")
    private BigDecimal halfDeposit;

    /* 半月累计流水 */
    @Schema(title = "半月累计流水")
    private BigDecimal halfValidAmount;
}
