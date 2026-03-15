package com.cloud.baowang.play.wallet.vo.req.db.sh.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class PlayerBettingNotifyVO {

    private Long transferNo;        // 转账编号
    private String transferType;    // 转账类型，如 PAYOUT
    private Integer gameTypeId;     // 游戏类型ID
    private String roundNo;         // 局号
    private Long playerId;          // 玩家ID
    private String loginName;       // 登录名
    private Long payoutTime;        // 派彩时间（时间戳）
    private BigDecimal payoutAmount; // 派彩金额
    private String currency;        // 币种
    private List<BetRecordVO> bettingRecordList ;
}

