package com.cloud.baowang.user.api.vo.vip;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VIPSendRewardReqVO {

    //站点， 如果传了，就按当前站点来，忽略手动模式
    String siteCode;

    //手动标志， 如果是手动的，就按所有站点来算，如果不是手动的，就按当前时区来
    Boolean manualFlag = false;
}
