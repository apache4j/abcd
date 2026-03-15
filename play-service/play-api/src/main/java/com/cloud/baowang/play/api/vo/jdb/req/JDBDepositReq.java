package com.cloud.baowang.play.api.vo.jdb.req;

import lombok.Data;

import java.util.List;

@Data
public class JDBDepositReq {



    private Integer action;
    /** 当前系统时间 */
    private Long ts;

    /** 交易序号 - 给账变id对应*/
    private Long transferId;

    /** 玩家账号 */
    private String uid;

    private String currency;

    /** 要返还给玩家的金额（永远为正数）*/
    private Double amount;

    /** 此次存款与哪些提款相关；对应至 Action 13 的 transferId）*/
    private List<Long> refTransferIds;

    /** 玩家此次游戏期间的总押注金额 */
    private Double totalBet;

    /** 玩家此次游戏期间的总押注金额 */
    private Double totalWin;

    /** 游戏类型 */
    private Integer gType;

    /** 机台类型 */
    private Integer mType;



}
