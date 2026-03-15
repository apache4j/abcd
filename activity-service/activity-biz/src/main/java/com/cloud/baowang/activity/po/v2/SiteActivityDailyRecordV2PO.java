package com.cloud.baowang.activity.po.v2;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.cloud.baowang.common.mybatis.base.BasePO;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName(value = "site_activity_daily_record_v2")
public class SiteActivityDailyRecordV2PO extends BasePO {

    //订单号 唯一性 siteCode+dailyId+userId+role+day
    private String orderNo;

    private String siteCode; // 站点code
    private String dailyId; //竞赛ID
    private Integer venueType;//场馆类型
    private String userId; // 会员id
    private String userAccount; // 会员账号
    private Integer ranking; // 排名
    private BigDecimal awardAmount; // 奖励金额
    private BigDecimal awardPercentage; // 奖励百分比
    private Integer activityDiscountType; // 优惠方式:0:百分比,1:固定金额
    //奖励池总金额
    private BigDecimal totalAwardAmount;
    private Long day; // 排名发放日期

    private Integer role;//0=机器人,1=真实用户

    //奖励币种
    private String awardCurrency;

    //投注币种
    private String currency;

    //投注金额
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal betAmount;


    /**
     * 第一名机器人头像
     */
    private String roleIcon;

    public String getOrderNo() {
        return siteCode
                .concat("-")
                .concat(getDailyId())
                .concat("-")
                .concat(getUserId())
                .concat("-")
                .concat(getRole()+"")
                .concat("-")
                .concat(getDay()+"");
    }
}
