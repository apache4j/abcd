package com.cloud.baowang.play.api.vo.pp.res;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class PPFreeRoundResVO implements Serializable {

    //管理用户
    String currency;
    //游戏
    String gameIDList;
    //奖励次数
    Integer rounds;
    //已经用的次数
    Integer roundsPlayed;

    String bonusCode;
    //结束时间戳
    String expirationDate;

    String bonusId;
    //奖励回合数
    //游戏ID code
    String createDate;
    //注单大小.
    BigDecimal packageId;

}
