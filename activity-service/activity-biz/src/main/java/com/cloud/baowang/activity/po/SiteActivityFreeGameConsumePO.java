package com.cloud.baowang.activity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 免费旋转次数消费记录
 */
@Data
@NoArgsConstructor
@TableName(value = "site_activity_free_game_consume")
public class SiteActivityFreeGameConsumePO extends SiteBasePO {



    /**
     * 会员ID
     */
    private String userId;

    /**
     * 会员账号
     */
    private String userAccount;

    /**
     * 当前次数余额
     */
    private Integer balance;

    /**
     * 消耗次数
     */
    private Integer consumeCount;
    /**
     * 平台编号
     */
    private String venueCode;

    /**
     * 游戏ID
     */
    private String gameId;

    /**
     * 投注盈亏
     */
    private BigDecimal betWinLose;

    /**
     * 获取来源订单号
     */
    private String orderNo;

    /**
     * 交易号唯一。不会重复
     */
    private String betId;
    /**
     * 币种 CommonConstant.PLAT_CURRENCY_CODE
     */
    private String currencyCode;


}
