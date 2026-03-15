package com.cloud.baowang.play.api.vo.jdb.req;

import lombok.Data;

import java.util.List;

@Data
public class JDBSettleReq extends JDBBaseReq {

    /** 要返还给玩家的金额（永远为正数） */
    private Double amount;

    /** 游戏局号 */
    private String gameRoundSeqNo;

    /** 此次结算了哪些注单；对应至 Action 9 的 transferId */
    private List<Long> refTransferIds;

    /** 游戏序号 */
    private String historyId;

    /** 报表日期（dd-MM-yyyy） */
    private String reportDate;

    /** 游戏日期（dd-MM-yyyy HH:mm:ss） */
    private String gameDate;

    /** 最后修改时间（dd-MM-yyyy HH:mm:ss） */
    private String lastModifyTime;

    /** 押注金额 */
    private Double bet;

    /** 有效投注金额 */
    private Double validBet;

    /** 游戏赢分 */
    private Double win;

    /** 总输赢 */
    private Double netWin;

    /** 税 */
    private Double tax;


    /** 回馈金 */
    private Double commission;

    /** 当局是否结束 */
    private Boolean roundClosed;

    public boolean valid(){
        return action != null
                && ts != null
                && transferId != null
                && uid != null
                && currency != null
                && gType != null
                && mType != null
                && (refTransferIds != null && !refTransferIds.isEmpty());
    }
}
