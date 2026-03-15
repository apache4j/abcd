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
public class PPPromoWinReqVO extends PPBaseReqVO {

    String hash;
    String providerId;
    String timestamp;
    String userId;

    String campaignId;

    String campaignType;
    BigDecimal amount;
    String currency;
    String reference;

    String roundId;

    String gameId;

    String dataType;


    public boolean isValid() {
        return
                StrUtil.isNotEmpty(this.getHash()) &&
                StrUtil.isNotEmpty(this.getProviderId())&&
                this.getTimestamp()!=null &&
                StrUtil.isNotEmpty(this.getUserId()) &&
                StrUtil.isNotEmpty(this.getCampaignId()) &&
                StrUtil.isNotEmpty(this.getCampaignType()) &&
                this.getAmount()!=null &&
                StrUtil.isNotEmpty(this.getCurrency())

                ;
    }

}
