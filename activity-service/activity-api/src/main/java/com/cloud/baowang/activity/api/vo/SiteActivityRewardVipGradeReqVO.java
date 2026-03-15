package com.cloud.baowang.activity.api.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @className: SiteActivityRewardVipGradePO
 * @author: wade
 * @description:
 * @date: 7/9/24 15:39
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "站点活动VIP等级奖励配置")
public class SiteActivityRewardVipGradeReqVO implements Serializable {
    /**
     * 站点code
     */
    @Schema(description = "站点code",hidden = true)
    private String siteCode;

    /**
     * VIP等级code
     */
    @Schema(description = "VIP等级code")
    private Integer vipGradeCode;

    /**
     * VIP等级名称
     */
    @Schema(description = "VIP等级名称")
    private String vipGradeName;

    /**
     * VIP段位code
     */
    @Schema(description = "VIP段位code")
    private Integer vipRankCode;

    /**
     * vip段位名称
     */
    @Schema(description = "VIP段位名称")
    private String vipRankName;



    /**
     * 活动模板
     */
    @Schema(description = "活动模板")
    private String activityTemplate;

    /**
     * 活动id
     */
    @Schema(description = "活动ID",hidden = true)
    private String baseId;

    /**
     * 领取次数
     */
    @Schema(description = "领取次数")
    private Integer rewardCount;


}
