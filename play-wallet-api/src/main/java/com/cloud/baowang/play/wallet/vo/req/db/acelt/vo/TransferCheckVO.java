package com.cloud.baowang.play.wallet.vo.req.db.acelt.vo;

import lombok.Data;

@Data
public class TransferCheckVO {

    /** 商户帐号 */
    private String merchantCode;

    /** 玩家帐号 */
    private String userName;

    /** 转帐类型 */
    private String transferType;

    /** 转帐金额 */
    private String amount;

    /** 转帐流水号 */
    private String transferId;

    /** 安全回调类型 */
    private String safetyType;

    /** 当前时间时间戳 */
    private String timestamp;

    /** MD5 签名 */
    private String signature;

    @Override
    public String toString() {
        return  merchantCode+"&"+userName+"&"+transferType+"&"+amount+"&"+transferId+"&"+timestamp;
    }
}

