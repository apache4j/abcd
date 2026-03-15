package com.cloud.baowang.wallet.api.vo.activityV2;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserAwardRecordV2ReqVO implements Serializable {

    private String siteCode;
    private String vipGradeCode;
    private String userId;
    //0 升级奖励， 1， 周奖励， 4， 生日奖励
    private String awardType;
    //ActivityReceiveStatusEnum
    private Integer receiveStatus;

    private String orderId;
}
