package com.cloud.baowang.play.game.dg2.vo;
import lombok.Data;

import java.lang.String;
import java.math.BigDecimal;

/**
 * DG2 游戏历史记录
 */
@Data
public class DG2GameHistory {

    /** 注单ID(唯一) */
    private Long id;

    /** 游戏桌号(红包小费记录没有) */
    private Integer tableId;

    /** 游戏靴号(红包小费记录没有) */
    private Long shoeId;

    /** 当靴局号(红包小费记录没有) */
    private Long playId;

    /** 游戏厅号 (1:旗舰厅；2:亚洲厅；3，4:现场厅；5:性感厅；8,9:区块链厅) */
    private Integer lobbyId;

    /** 注单类型 (1:注单，2: 红包小费) */
    private Integer gameType;

    /** 游戏类型(百家乐，龙虎等) */
    private Integer gameId;

    /** 下注时间 */
    private String betTime;

    /** 结算时间 */
    private String calTime;

    /** 派彩金额(含本金) */
    private BigDecimal winOrLoss;

    /** 下注前余额(仅作参考) */
    private BigDecimal balanceBefore;

    /** 下注金额(下注扣款金额) */
    private BigDecimal betPoints;

    /** 洗码金额(用于计算佣金) */
    private BigDecimal availableBet;

    /** 会员账号([a-zA-Z0-9@#_]{3,40}) */
    private String userName;

    /** 游戏结果 */
    private String result;

    /** 注单详情 */
    private String betDetail;

    /** 客户端IP */
    private String ip;

    /** 游戏唯一局号 */
    private String ext;

    /** 结算状态 (0:未结算，1:已结算，2:已撤销，3: 冻结) */
    private Integer isRevocation;

    /** 改单时对应的注单记录 */
    private Long parentBetId;

    /** 币种ID(请参考对应关系说明) */
    private Integer currencyId;

    /** 客户端平台ID (1: PC 2: 安卓 3: 苹果 5: H5) */
    private Integer deviceType;

    /** 钱包扣款记录(转账模式API没有) */
    private String transfers;

    private String betResult;

    private String playType;

    private String winner;

    private String playInfo;
}
