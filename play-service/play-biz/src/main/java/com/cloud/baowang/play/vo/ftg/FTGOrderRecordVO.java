package com.cloud.baowang.play.vo.ftg;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class FTGOrderRecordVO {

    /**
     * 注单编号
     * 是：注单编号
     */
    private String id;

    /**
     * 下注时间
     * 是：下注时间，格式参考ISO 8601时间格式（例如：2019-05-09T01:05:20+00:00）
     */
    private String bet_at;  // 下注时间，ISO 8601 格式

    /**
     * 修改时间
     * 是：修改时间，格式参考ISO 8601时间格式（例如：2019-05-09T01:05:20+00:00）
     */
    private String modified_at;  // 修改时间，ISO 8601 格式

    /**
     * 结算时间
     * 是：结算时间，格式参考ISO 8601时间格式（例如：2019-05-09T01:05:20+00:00）
     */
    private String payoff_at;  // 结算时间，ISO 8601 格式

    /**
     * 下注日期
     * 是：下注日期，时区使用UTC+0。格式为YYYY-MM-DD（例如：2019-05-09）
     */
    private String round_date;  // 下注日期，格式为 YYYY-MM-DD，时区为 UTC+0

    /**
     * 游戏大堂编号
     * 是：游戏大堂编号，详细请参考 6.3. 游戏大堂清单
     */
    private String lobby_id;  // 游戏大堂编号

    /**
     * 游戏编号
     * 是：游戏编号
     */
    private Integer game_id;  // 游戏编号

    /**
     * 游戏分组编号
     * 是：游戏分组编号
     */
    private Integer game_group_id;  // 游戏分组编号

    /**
     * 帐号
     * 是：用户的账号
     */
    private String username;  // 帐号

    /**
     * 下注金额
     * 是：下注金额
     */
    private BigDecimal bet_amount;  // 下注金额

    /**
     * 派彩金额
     * 是：派彩金额
     */
    private BigDecimal payoff;  // 派彩金额

    /**
     * 损益
     * 是：损益 = 派彩金额 - 下注金额
     */
    private BigDecimal profit;  // 损益 = 派彩金额 - 下注金额

    /**
     * 余额
     * 是：账户余额
     */
    private BigDecimal balance;  // 余额

    /**
     * 退水金额
     * 是：给玩家的回馈金。若未使用该字段，始终为0
     */
    private BigDecimal commission;  // 退水金额

    /**
     * 有效投注总和
     * 是：有效投注 = 下注金额 - 退水金额。若注单作废，值为0
     */
    private BigDecimal commissionable;  // 有效投注总和

    /**
     * 币别
     * 是：使用的币别，参考 6.2. 支援币别
     */
    private String currency;  // 币别

    /**
     * 下注来源
     * 是：下注来源：
     * 0：网頁
     * 1：Android
     * 2：iOS
     * 3：mweb
     */
    private Integer device;  // 下注来源

    /**
     * 来源版本
     * 是：下注来源的版本
     */
    private String device_version;  // 来源版本

    /**
     * 注单结果
     * 是：注单结果，详细请参考 6.5. 注单结果
     */
    private String result;  // 注单结果

    /**
     * 特色游戏
     * 否：BG：BonusGame，奖品游戏；FG：FreeGame，免费游戏；JP：Jackpot，游戏大奖
     */
    private String feature_game;  // 特色游戏

    /**
     * 游戏类型
     * 是：游戏类型，如 slots、table、fishing、arcade
     */
    private String category;  // 游戏类型

}
