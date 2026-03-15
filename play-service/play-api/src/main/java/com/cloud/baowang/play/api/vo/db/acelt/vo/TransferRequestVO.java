package com.cloud.baowang.play.api.vo.db.acelt.vo;

import lombok.Data;

import java.util.List;

@Data
public class TransferRequestVO {

    /** 帐变类型 */
    private String transferType;

    /** 讯息号 */
    private String notifyId;

    /** 当前时间戳 */
    private String timestamp;

    /** MD5 签名 */
    private String sign;

    /** 交易数据集合 */
    private List<TransferData> transferDatas;


    @Override
    public String toString() {
        return "notifyId" + notifyId + "timestamp" + timestamp + "transferType" + transferType;
    }
}

