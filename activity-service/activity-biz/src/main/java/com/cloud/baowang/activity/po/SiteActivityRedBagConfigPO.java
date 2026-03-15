package com.cloud.baowang.activity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

import java.io.Serializable;

/**
 * 红包雨活动配置附表
 * @TableName site_activity_red_bag_config
 */
@TableName(value ="site_activity_red_bag_config")
@Data
public class SiteActivityRedBagConfigPO extends BasePO implements Serializable {

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
     * 有效红包数量上限
     */
    private Integer redBagMaximum;

    /**
     * 红包金额类型 1 固定金额 2 随机金额
     */
    private Integer amountType;

    /**
     * 序号
     */
    private Integer sort;
}