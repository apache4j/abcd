package com.cloud.baowang.activity.po.v2;


import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName(value = "site_activity_new_hand")
public class SiteActivityNewHandPO extends BasePO implements Serializable {

    /**
     * 站点code
     */
    private String siteCode;

    /**
     * 所属活动
     */
    private String activityId;

    /**
     * 派发方式0.过期作废，1.过期自动派发，2.立即派发
     */
    private Integer distributionType;

    /**
     * 参与方式，0.手动参与，1.自动参与
     */
    private Integer participationMode;

    /**
     * 匹配条件首次存款
     */
    private String conditionFirstDeposit;

    /**
     * 匹配条件首次提款
     */
    private String conditionFirstWithdrawal;

    /**
     * 匹配条件签到
     */
    private String conditionSignIn;

    /**
     * 匹配条件负盈利
     */
    private String conditionNegativeProfit;

    /**
     * 活动币种类型（0.平台币，1. 法币）
     */
    private String platformOrFiatCurrency;
}
