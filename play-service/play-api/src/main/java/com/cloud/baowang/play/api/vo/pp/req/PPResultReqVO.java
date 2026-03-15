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
public class PPResultReqVO extends PPBaseReqVO {

    String hash;
    String userId;
    String gameId;
    String roundId;
    BigDecimal amount;
    String reference;
    String providerId;

    String timestamp;
    String roundDetails;
    String bonusCode;

    BigDecimal promoWinAmount;

    //{gameId=vs20olympgold, reference=6846847efa0c1c256bace5c5, roundDetails=spin,
    // amount=1.0, providerId=PragmaticPlay, roundId=750914410000, userId=Utest_63460974,
    // hash=d4b26d841a63a157b4e7c03d0561deea, timestamp=1749451902726}
    public boolean isValid() {
        return
                StrUtil.isNotEmpty(this.getHash()) &&
                StrUtil.isNotEmpty(this.getUserId()) &&
                StrUtil.isNotEmpty(this.getGameId())&&
                StrUtil.isNotEmpty(this.getRoundId())&&
                this.getAmount()!=null&&
                StrUtil.isNotEmpty(this.getReference())

                ;
    }

}
