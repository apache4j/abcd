package com.cloud.baowang.play.api.vo.sba;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SBSettleDetailReq {

    /** 用户 id */
    private String userId;

    /** 唯一 id */
    private String refId;

    /** 沙巴系统交易 id */
    private String txId;

    /** 更新时间 (yyyy-MM-dd HH:mm:ss.SSS) GMT-4 */
    private String updateTime;

    /** 决胜时间 (仅显示日期) (yyyy-MM-dd 00:00:00.000) GMT-4 */
    private String winlostDate;

    /** 交易结果 */
    private String status;

    /** 注单赢回的金额 */
    private BigDecimal payout;

    /** 需增加在玩家的金额 */
    private BigDecimal creditAmount;

    /** 需从玩家扣除的金额 */
    private BigDecimal debitAmount;


    /** 在以下情况下会有值：
     * - 注单被 cashout -> "extraStatus": "cashout" 这种情况会送出 settle API。
     * - 注单 cashout 后，注单被 void -> "extraStatus": "void ticket"这种情况会送出 resettle API。
     * - 注单 cashout 后，赛事被 refund -> "extraStatus": "refund match" 这种情况会送出 resettle API。
     * - 注单 cashout 后，cashout 行为被 void 取消-> "extraStatus": "void cashout" 这种情况会送出 unsettle API。
     */
    private String extraStatus;

    public Boolean validAmount(){
        return this.creditAmount.compareTo(BigDecimal.ZERO) <= 0 && this.debitAmount.compareTo(BigDecimal.ZERO) <= 0;
    }



}

