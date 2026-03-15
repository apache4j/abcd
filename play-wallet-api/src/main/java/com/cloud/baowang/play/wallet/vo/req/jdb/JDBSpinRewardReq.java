package com.cloud.baowang.play.wallet.vo.req.jdb;

import lombok.Data;

@Data
public class JDBSpinRewardReq extends JDBBaseReq {

    /** 下注金额（永远为正数） */
    private Double amount;

    /** 活动序号 */
    private String activityNo;


    /** 活动序号 */
    private String activityName;


    /** 活动序号 */
    private String activityDate;


    public boolean valid(){
        return action != null
                && ts != null
                && transferId != null
                && uid != null
                && currency != null
                && (amount != null && amount>0);


    }
}
