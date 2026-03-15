package com.cloud.baowang.activity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @description 兑换码详情表,每个币种的兑换码都会保存一条记录
 * @author brence
 * @date 2025-10-27
 */
@Data
@NoArgsConstructor
@TableName("site_activity_redemption_code_detail_info")
public class SiteActivityRedemptionCodeDetailPO extends BasePO {

    private static final long serialVersionUID = 1L;

    /**
     * 活动ID，关联兑换码主键id
     */
    private Long activityId;

    /**
     * 兑换码类型，0:通用兑换码，1:唯一兑换码
     */
    private Integer category;

    /**
     * 站点编码
     */
    private String siteCode;

    /**
     * 兑换使用人数上限，兑换码类型=0，且top_limit=0时，表示为通用码，没有兑换人数限制；兑换码类型=1时，top_limit=1，1人1码
     */
    private Integer topLimit;

    /**
     * 兑换条件，1:无限制用户，2:存款用户，3：当天存款用户（兑换码生效当天）；4：三天内存款用户
     */
    private Integer condition;

    /**
     * 奖金
     */
    private BigDecimal award;

    /**
     * 兑换码数量
     */
    private Integer quantity;

    /**
     * 币种
     */
    private String currency;

    /**
     * 订单号，活动兑换码主表中的order_no，作为冗余字段，不需要查主表
     */
    private String orderNo;

    /**
     * 流码倍率，可以针对每种币种设置不同的值
     */
    private BigDecimal washRatio;

    /**
     * 兑换码生效时间，精确到秒
     */
    private Long startTime;

    /**
     * 兑换码失效时间
     */
    private Long endTime;
}
