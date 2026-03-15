package com.cloud.baowang.play.api.vo.pp.req;


import cn.hutool.core.util.StrUtil;
import com.cloud.baowang.play.api.vo.pp.PPBaseReqVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PPJackpotWinReqVO extends PPBaseReqVO {

    String hash;
    String providerId;
    Long timestamp;
    String userId;
    String gameId;
    String roundId;
    String jackpotId;
    BigDecimal amount;
    String reference;

    String roundDetails;
    String bonusCode;


    public boolean isValid() {
        return
                StrUtil.isNotEmpty(this.getHash()) &&
                        StrUtil.isNotEmpty(this.getProviderId())&&
                        this.getTimestamp()!=null &&
                StrUtil.isNotEmpty(this.getUserId()) &&
                StrUtil.isNotEmpty(this.getGameId()) &&
                StrUtil.isNotEmpty(this.getRoundId()) &&
                StrUtil.isNotEmpty(this.getJackpotId())

                ;
    }

}
