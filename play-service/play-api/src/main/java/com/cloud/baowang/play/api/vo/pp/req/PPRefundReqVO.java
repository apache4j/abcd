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
public class PPRefundReqVO extends PPBaseReqVO {

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


    public boolean isValid() {
        return
                StrUtil.isNotEmpty(this.getHash()) &&
                StrUtil.isNotEmpty(this.getUserId())&&
                StrUtil.isNotEmpty(this.getReference())&&
                StrUtil.isNotEmpty(this.getProviderId())

                ;
    }

}
