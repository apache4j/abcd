package com.cloud.baowang.play.wallet.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

/**
 * SEXY平台订单记录
 */
@Data
@TableName("sexy_order_record")
public class SexyOrderRecordPO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 游戏商注单号
     */
    private String platformTxId;

    /**
     * 玩家帐号
     */
    private String userId;

    /**
     * 玩家三方帐号
     */
    private String venueUserId;


    /**
     * 玩家使用的货币代码
     */
    private String currency;

    /**
     * 游戏平台名称
     */
    private String platform;

    /**
     * 平台游戏类型
     */
    private String gameType;

    /**
     * 平台游戏代码
     */
    private String gameCode;

    /**
     * 游戏名称
     */
    private String gameName;

    /**
     * 游戏平台的下注项目
     */
    private String betType;

    /**
     * 下注金额
     */
    private BigDecimal betAmount;

    /**
     * 玩家下注时间（时间戳）
     */
    private Long betTime;

    /**
     * 游戏商的回合识别码
     */
    private String roundId;

    /**
     * 游戏商提供的游戏信息，不验证内容
     */
    private String gameInfo;

    /**
     * 特殊收费游戏标记
     */
    private Boolean isPremium;

    /**
     * 调整投注
     */
    private BigDecimal adjustAmount;

    /**
     * 返还金额(包含下注金额)
     */
    private BigDecimal winAmount;

    /**
     * 打赏金额
     */
    private BigDecimal tip;

    /**
     * 有效投注金额
     */
    private BigDecimal turnover;

    /**
     * 辨认交易时间依据-ISO8601
     */
    private Long txTime;

    /**
     * 注单更新时间（时间戳）
     */
    private Long updateTime;

    /**
     * 用此注单号作为结算的参考
     */
    private String refPlatformTxId;

    /**
     * 结算方法
     */
    private String settleType;

    /**
     * 桌台号
     */
    private String deskNo;

    /* ------------- */

    /**
     * 净输赢
     */
    private BigDecimal winLoss;

    /**
     * 结果牌
     */
    private String  resultList ;

    /**
     * 赢家
     */
    private String  winner ;

    /**
     * 赔率
     */
    private String  odds ;


    /**
     * ip
     */
    private String  ip ;


    /**
     * 订单状态 输/赢
     */
    private String status ;


    /**
     * 订单类型 1-下注 2-取消下注 3-交易作废 4-结算 5-派彩取消 6-重派彩 7-打赏 8-取消打赏
     */
    private String orderType ;


}
