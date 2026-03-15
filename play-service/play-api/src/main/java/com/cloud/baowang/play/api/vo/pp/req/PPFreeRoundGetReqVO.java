package com.cloud.baowang.play.api.vo.pp.req;

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
public class PPFreeRoundGetReqVO implements Serializable {

    //管理用户
    String userId;
    //奖励码
    String bonusCode;

    String venueCode;

    public boolean isValid() {
        return
                StrUtil.isNotEmpty(this.getUserId()) &&
                        StrUtil.isNotEmpty(this.getBonusCode()) &&
                        StrUtil.isNotEmpty(this.getVenueCode())
                ;
    }

}
