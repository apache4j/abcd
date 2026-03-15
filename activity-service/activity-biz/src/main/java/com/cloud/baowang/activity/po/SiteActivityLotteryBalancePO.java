package com.cloud.baowang.activity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @className: SiteActivityLotteryBalancePO
 * @author: wade
 * @date: 11/9/24 09:04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "site_activity_lottery_balance")
public class SiteActivityLotteryBalancePO extends SiteBasePO implements Serializable {


    /**
     * 会员ID
     */
    private String userId;

    /**
     * 会员账号
     */
    private String userAccount;

    /**
     * 当前抽奖次数余额
     */
    private Integer balance;


}