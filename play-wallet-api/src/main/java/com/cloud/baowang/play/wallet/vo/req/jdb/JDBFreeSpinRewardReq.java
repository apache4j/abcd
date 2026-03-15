package com.cloud.baowang.play.wallet.vo.req.jdb;

import lombok.Data;

@Data
public class JDBFreeSpinRewardReq {



    private Integer action;
    /** 当前系统时间 */
    private Long ts;

    /** 交易序号 */
    private String transferId;

    /** 免费场次序号 */
    private String eventId;

    /** 玩家账号 */
    private String uid;

    private Double amount;

    private String currency;

    /** 累计押注 */
    private Double accumulatedTurnover;

    /** 累计赢分 */
    private Double accumulatedWin;

    /** 玩家提取时间 */
    private String cashoutTime;



}
