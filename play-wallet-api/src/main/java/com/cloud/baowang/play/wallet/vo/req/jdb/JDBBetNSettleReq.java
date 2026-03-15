package com.cloud.baowang.play.wallet.vo.req.jdb;

import lombok.Data;

@Data
public class JDBBetNSettleReq extends JDBBaseReq {

    private String historyId;

    private String reportDate;

    private String gameDate;

    private Double bet;

    private Double mb;

    private Double win;

    private Double netWin;

    private String lastModifyTime;

    private String sessionNo;

    public boolean valid(){
        return action != null
                && ts != null
                && transferId != null
                && uid != null
                && currency != null
                && gType != null
                && mType != null
                && (bet != null && bet>0)
                && (mb != null && mb>0);

    }

}
