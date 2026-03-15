package com.cloud.baowang.play.wallet.vo.req.jdb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JDBBaseReq implements Serializable {

    protected Integer action;
    /** 当前系统时间 */
    protected Long ts;

    /** 交易序号 */
    protected String transferId;

    /** 玩家账号 */
    protected String uid;

    /** 币种 */
    protected String currency;

    /** 游戏类型 */
    protected Integer gType;

    /** 机台类型 */
    protected Integer mType;


    protected String systemSessionId;

}
