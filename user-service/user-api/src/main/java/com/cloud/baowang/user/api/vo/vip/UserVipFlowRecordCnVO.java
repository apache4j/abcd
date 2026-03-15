package com.cloud.baowang.user.api.vo.vip;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author mufan
 * @Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserVipFlowRecordCnVO  implements Serializable {
    @Schema(description = "会员ID全局唯一")
    private String userId;

    @Schema(description = "站点code")
    private String siteCode;

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "当前VIP等级code")
    private Integer vipGradeCode;

    @Schema(description = "VIP升级后等级code")
    private Integer nextVipGradeCode;

    @Schema(description = "当前VIP流水金额")
    private BigDecimal finishBetAmount;

    @Schema(description = "VIP升级总流水金额")
    private BigDecimal upgradeBetAmount;

    @Schema(description = "保级流水金额")
    private BigDecimal finishRelegationAmount;

    @Schema(description = "保级总流水金额")
    private BigDecimal gradeRelegationAmount;

    @Schema(description = "保级天数")
    private Integer relegationDays;

    @Schema(description = "升级到VIP等级的初始时间yyyy-mm-dd")
    private String upVipTime;

    @Schema(description = "降级时间")
    private String relegationDaysTime;

    @Schema(description = "币种")
    private String currencyCode;
}
