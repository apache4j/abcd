package com.cloud.baowang.play.wallet.vo.req.db.sh.vo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
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
