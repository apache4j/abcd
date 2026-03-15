package com.cloud.baowang.play.wallet.vo.req.db.sh.vo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class SettleParams {
    private BigDecimal payoutAmount;
    private Long  payoutTime;
    private String gameTypeId;
    private Long transferNo;
    private String loginName;
    private String playerId;
    private String transferType;
    private Map<String,BigDecimal> betPayoutMap;
    private String currency;
    private String roundNo;

}
