package com.cloud.baowang.play.api.vo.db.sh.vo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class CancelBetParams {
    private String gameTypeId;
    private Long cancelTime;
    private Long transferNo;
    private String loginName;
    private Map<String,BigDecimal> betPayoutMap;
    private Integer hasTransferOut;
    private String roundNo;
}
