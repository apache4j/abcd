package com.cloud.baowang.play.api.vo.pp.req;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.baowang.common.core.utils.BigDecimalUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class PPFreeRoundGiveReqVO implements Serializable {

    //管理用户
    String userId;
    //奖励码
    String bonusCode;

    String venueCode;
    //开始时间戳
    Long startDate;
    //结束时间戳
    Long expirationDate;
    //奖励回合数
    Integer rounds;
    //游戏ID code
    String gameId;
    //注单大小.
    BigDecimal betPerLine;

    String siteCode;

    public boolean isValid() {
        return
            StrUtil.isNotEmpty(this.getUserId()) &&
            StrUtil.isNotEmpty(this.getBonusCode()) &&
            StrUtil.isNotEmpty(this.getVenueCode())&&
            ObjUtil.isNotNull(this.getStartDate()) &&
            ObjUtil.isNotNull(this.getExpirationDate()) &&
            ObjUtil.isNotNull(this.getRounds()) &&
            StrUtil.isNotEmpty(this.getGameId()) &&
            ObjUtil.isNotNull(this.getBetPerLine())
        ;
    }

}
