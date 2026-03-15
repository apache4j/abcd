package com.cloud.baowang.activity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 红包雨活动段位中奖配置附表
 *
 * @TableName site_activity_red_bag_rank_config
 */
@TableName(value = "site_activity_red_bag_rank_config")
@Data
public class SiteActivityRedBagRankConfigPO extends BasePO implements Serializable {

    /**
     * 活动base表id
     */
    private String baseId;

    /**
     * 站点code
     */
    private String siteCode;

    /**
     * 段位code
     */
    private Integer vipRankCode;

    /**
     * 固定金额
     */
    @Schema(title = "固定金额")
    private BigDecimal fixedAmount;

    /**
     * 随机金额 起始值
     */
    @Schema(title = "随机金额 起始值")
    private BigDecimal randomStartAmount;

    /**
     * 随机金额 结束值
     */
    @Schema(title = "随机金额 结束值")
    private BigDecimal randomEndAmount;
    /**
     * 中奖概率
     */
    @Schema(title = "中奖概率")
    private BigDecimal hitRate;

    /**
     * 序号
     */
    private Integer sort;
}