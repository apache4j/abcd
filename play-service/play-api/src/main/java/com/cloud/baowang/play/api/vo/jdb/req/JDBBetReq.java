package com.cloud.baowang.play.api.vo.jdb.req;

import lombok.Data;

@Data
public class JDBBetReq extends JDBBaseReq {

    /** 下注金额（永远为正数） */
    private Double amount;

    /** 游戏局号 */
    private String gameRoundSeqNo;


    public boolean valid(){
        return action != null
                && ts != null
                && transferId != null
                && uid != null
                && currency != null
                && gType != null
                && mType != null
                && (amount != null )
                && gameRoundSeqNo != null;

    }
}
