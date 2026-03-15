package com.cloud.baowang.play.api.vo.jdb;


import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class JdbGameHistory {

    /** 游戏序号，参照附录 historyId 说明 */
    private String historyId;

    /** 游戏轮次编号（部分记录会有） */
    private String gameRoundSeqNo;

    /** 玩家账号 */
    private String playerId;

    /** 游戏类型，参照附录 游戏提供商 */
    @JSONField(name = "gType")
    private Integer gType;

    /** 机台类型 */
    private Integer mtype;

    /** 房间类型（部分棋牌或对战类游戏才有） */
    private Integer roomType;

    /** 游戏时间（格式：dd-MM-yyyy HH:mm:ss） */
    private String gameDate;

    /** 押注金额 */
    private Double bet;

    /** 赌注金额（如有 gamble 行为时才出现） */
    private Double gambleBet;

    /** 游戏赢分 */
    private Double win;

    /** 抽税金额（部分游戏可能含有） */
    private Double tax;

    /** 总输赢 */
    private Double total;

    /** 货币别，参照附录 货币代码 */
    private String currency;

    /** 奖池中奖金额（若有） */
    private Double jackpot;

    /** 奖池贡献（若有） */
    private Double jackpotContribute;

    /** 投注面值 */
    private Double denom;

    /** 最后修改时间（格式：dd-MM-yyyy HH:mm:ss） */
    private String lastModifyTime;

    /** 进场金额（字符串形式） */
    private String beforeBalance;

    /** 离场金额（字符串形式） */
    private String afterBalance;

    /** 玩家 IP 地址 */
    private String playerIp;

    /** 客户端类型（WEB, desktop 等） */
    private String clientType;

    /** 是否包含免费游戏（0/1） */
    private Integer hasFreegame;

    /** 是否包含奖金游戏（0/1） */
    private Integer hasBonusGame;

    /** 是否包含赌博功能（0/1） */
    private Integer hasGamble;

    /** 系统抽成或赢分（可能为 0） */
    private Double systemTakeWin;

    /** 游戏有效投注（部分游戏记录会包含） */
    private Double validBet;

    /** 佣金（部分游戏记录会包含） */
    private Double commission;

    /** 转账编号 */
    private Long transferId;

    /** 游戏名称（部分记录可能为空） */
    private String gameName;

    /** 游戏轮次号（部分记录可能含有） */
    private String roundSeqNo;
}