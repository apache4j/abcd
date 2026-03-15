package com.cloud.baowang.play.wallet.vo.req.jdb;

import lombok.Data;

@Data
public class JDBWithdrawReq {



    private Integer action;
    /** 当前系统时间 */
    private Long ts;

    /** 交易序号 - 给账变id对应*/
    private Long transferId;

    /** 玩家账号 */
    private String uid;

    private String currency;
    /** 游戏类型 */
    private Integer gType;

    /** 机台类型 */
    private Integer mType;

    /** 提款金额（永远为正数）提款-提到三方 取消提款-返给玩家 */
    private Double amount;


}
