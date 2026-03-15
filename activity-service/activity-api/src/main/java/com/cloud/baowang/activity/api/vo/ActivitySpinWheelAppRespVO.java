package com.cloud.baowang.activity.api.vo;


import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 转盘活动返回活动详情
 */
@Data
@Schema(title = "转盘活动返回活动详情-app")
@I18nClass
public class ActivitySpinWheelAppRespVO extends ActivityBasePartRespVO {


    /**
     * 剩余抽奖次数
     */
    @Schema(description = "剩余抽奖次数")
    private Integer balanceCount;

    /**
     * 奖金总计
     */
    @Schema(description = "奖金总计")
    private BigDecimal totalAmount;





    @Schema(description = "bronze转盘活动奖励详情配置")
    private List<SiteActivityRewardSpinAPPResponseVO> bronze;

    @Schema(description = "silver转盘活动奖励详情配置")
    private List<SiteActivityRewardSpinAPPResponseVO> silver;

    @Schema(description = "gold转盘活动奖励详情配置")
    private List<SiteActivityRewardSpinAPPResponseVO> gold;

    @Schema(description = "vip段位配置")
    private List<SiteVIPRankResVO> vipRankConfig;


    /**
     * vip等级
     */
    @Schema(description = "vip等级")
    private Integer vipGradeCode;

    /**
     * vip等级名称
     */
    @Schema(description = "vip等级名称")
    private String vipGradeCodeName;
    /**
     * vip等级
     */
    @Schema(description = "vip段位")
    private Integer vipRankCode;

    /**
     * vip段位名称
     */
    @Schema(description = "vip段位名称")
    private String vipRankCodeName;

}