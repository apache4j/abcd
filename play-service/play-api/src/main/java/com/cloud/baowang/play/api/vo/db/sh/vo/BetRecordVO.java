package com.cloud.baowang.play.api.vo.db.sh.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BetRecordVO {

    private Long id;                    // 记录ID
    private Long playerId;              // 玩家ID
    private String playerName;          // 玩家名称
    private Long agentId;               // 代理ID
    private BigDecimal betAmount;       // 投注金额
    private BigDecimal validBetAmount;  // 有效投注金额
    private BigDecimal netAmount;       // 输赢金额
    private BigDecimal beforeAmount;    // 投注前余额
    private Integer gameTypeId;         // 游戏类型ID
    private Integer platformId;         // 平台ID
    private String platformName;        // 平台名称
    private Integer betStatus;          // 投注状态
    private Integer betFlag;            // 投注标志
    private Integer betPointId;         // 投注点ID
    private String judgeResult;         // 判断结果
    private String currency;            // 币种
    private String tableCode;           // 桌号
    private String roundNo;             // 局号
    private String bootNo;              // 靴号
    private String loginIp;             // 登录IP
    private Integer deviceType;         // 设备类型
    private String deviceId;            // 设备ID
    private Integer recordType;         // 记录类型
    private Integer gameMode;           // 游戏模式
    private String nickName;            // 玩家昵称
    private String dealerName;          // 荷官名称
    private String tableName;           // 桌台名称
    private String agentCode;           // 代理代码
    private String agentName;           // 代理名称
    private String betPointName;        // 投注点名称（如“庄”、“闲”等）
    private String gameTypeName;        // 游戏类型名称
    private BigDecimal payAmount;       // 派彩金额
    private BigDecimal adddec1;         // 增减字段1
    private BigDecimal adddec2;         // 增减字段2
    private BigDecimal adddec3;         // 增减字段3
}
