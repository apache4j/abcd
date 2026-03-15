package com.cloud.baowang.user.api.vo.vip;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author : kimi
 * @Date : 26/6/23 11:23 AM
 * @Version : 1.0
 */
@Data
@Schema(description ="客户端获取VIP等级返回福利")
public class VIPGradeBenefitsAPP {
    // VIP展示卡片
    @Schema(description = "VIP等级编码")
    private Integer vipGradeCode;

    @Schema(description = "VIP等级名称")
    private String vipGradeName;

    @Schema(description = "VIP图标地址")
    private String vipIcon;

    @Schema(description = "VIP图标地址")
    private String vipIconImage;

    @Schema(description = "升级流水经验")
    private BigDecimal vipUpgradeExp;

    @Schema(description = "保级流水金额")
    private BigDecimal relegationAmount;

    // VIP等级特权
    @Schema(description = "VIP等级特权")
    private String vipGradePrivilege;

    @Schema(description = "保级天数")
    private Integer relegationDays;

    // 等级权益列表
    @Schema(description = "等级权益列表")
    private List<GetVIPAwardVO> vipAwardVOS;


    @Schema(description = "是否解锁")
    private Boolean vipGradeLock = false;
}
