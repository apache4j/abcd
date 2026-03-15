package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @className: VipUpRewardResVO
 * @author: wade
 * @description: vip晋级奖励转盘次数请求VO
 * @date: 30/10/24 09:32
 */
@Schema(description = "vip晋级奖励转盘次数请求VO")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@I18nClass
public class VipUpRewardResVO {
    /**
     * 站点code
     */
    private String siteCode;
    /**
     * vip奖励次数
     */
    @Schema(description = "vip奖励次数")
    @NotNull(message = "vip奖励次数不能为空")
    private Integer rewardCounts;

    /**
     * userId
     */
    @Schema(description = "userId")
    @NotNull(message = "userId不能为空")
    private String userId;


    /**
     * userAccount
     */
    @Schema(description = "userAccount")
    @NotNull(message = "userAccount不能为空")
    private String userAccount;

    /**
     * vip段位code
     */
    private Integer vipRankCode;

    /**
     * VIP等级code
     */
    private Integer vipGradeCode;

    /**
     * 奖励订单号
     */
    private String orderNumber;




}
