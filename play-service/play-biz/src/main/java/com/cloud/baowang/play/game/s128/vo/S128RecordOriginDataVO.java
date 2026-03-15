package com.cloud.baowang.play.game.s128.vo;


import lombok.Data;

import java.math.BigDecimal;

/**
 * S128注单实体
 */
@Data
public class S128RecordOriginDataVO {

    /**
     * 注单号码
     */
    private String ticketId;

    /**
     * 登录账号
     */
    private String loginId;

    /**
     * 赛场编号
     */
    private String arenaCode;
    /**
     * 赛场名中文名字
     */
    private String arenaNameCn;
    /**
     * 赛事编号
     */
    private String matchNo;
    /**
     * 赛事类型
     */
    private String matchType;

    /**
     * 赛事日期
     */
    private String matchDate;
    /**
     * 日场次
     */
    private Integer fightNo;
    /**
     * 赛事时间
     */
    private String fightDatetime;
    /**
     * 龍斗鸡
     */
    private String meronCock;
    /**
     * 龍斗鸡中文名字
     */
    private String meronCockCn;
    /**
     * 鳳斗鸡
     */
    private String walaCock;
    /**
     * 鳳斗鸡中文名字
     */
    private String walaCockCn;
    /**
     * 投注
     * MERON/WALA/BDD/FTD
     */
    private String betOn;

    /**
     * 赔率类型
     */
    private String oddsType;
    /**
     * 要求赔率
     */
    private String oddsAsked;
    /**
     * 给出赔率
     */
    private String oddsGiven;
    /**
     * 投注金额
     */
    private BigDecimal stake;
    /**
     * 奖金
     */
    private BigDecimal stakeMoney;
    /**
     * 转账前余额
     */
    private BigDecimal balanceOpen;
    /**
     * 转账后余额
     */
    private BigDecimal balanceClose;
    /**
     * 创建时间
     */
    private String createdDatetime;
    /**
     * 赛事结果
     * MERON/WALA/BDD/FTD
     */
    private String fightResult;
    /**
     * 赛事结果
     * WIN/LOSE/REFUND/CANCEL/VOID
     */
    private String status;
    /**
     * 输赢
     */
    private BigDecimal winloss;
    /**
     * 所得佣金
     */
    private BigDecimal commEarned;
    /**
     * 派彩
     */
    private BigDecimal payout;
    /**
     * 转账前余额
     */
    private BigDecimal balanceOpen1;
    /**
     * 转账后余额
     */
    private BigDecimal balanceClose1;

    /**
     * 处理时间
     */
    private String processedDatetime;

    /**
     * 税
     */
    private BigDecimal taxMoney;
}
