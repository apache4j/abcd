package com.cloud.baowang.play.api.vo.db.sh.vo;
import lombok.Data;

import java.math.BigDecimal;
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
