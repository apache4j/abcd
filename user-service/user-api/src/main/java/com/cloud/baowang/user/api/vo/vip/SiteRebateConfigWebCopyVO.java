package com.cloud.baowang.user.api.vo.vip;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
@Schema(description = "反水web对象")
@I18nClass
public class SiteRebateConfigWebCopyVO implements Serializable {

    /*  @Schema(description = "vip等级code")
      private String vipRankCode;

      @Schema(description = "vip等级名称")
      @I18nField
      private String vipRankName;*/
    @Schema(description = "vip等级code")
    private String vipGradeCode;

    @Schema(description = "vip等级名称")
    private String vipGradeName;

    @Schema(description = "体育返水配置")
    private BigDecimal sportsRebate;

    @Schema(description = "电竞返水配置")
    private BigDecimal esportsRebate;

    @Schema(description = "视讯返水配置")
    private BigDecimal videoRebate;

    @Schema(description = "棋牌返水配置")
    private BigDecimal pokerRebate;

    @Schema(description = "电子返水配置")
    private BigDecimal slotsRebate;

    @Schema(description = "彩票返水配置")
    private BigDecimal lotteryRebate;

    @Schema(description = "斗鸡返水配置")
    private BigDecimal cockfightingRebate;

    @Schema(description = "捕鱼返水配置")
    private BigDecimal fishingRebate;

    @Schema(description = "娱乐返水配置")
    private BigDecimal marblesRebate;

    @Schema(description = "单日返水上限")
    private BigDecimal dailyLimit;

    @Schema(description = "是否展示反水配置 0 不展示 1 展示")
    private Integer status;


}
