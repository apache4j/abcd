package com.cloud.baowang.play.api.vo.db.sh.vo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BetParams {
    private String gameTypeId;
    private BigDecimal betTotalAmount;
    private Long transferNo;
    private String loginName;
    private Long betTime;
    private String currency;
    private String roundNo;
    private List<BetInfo> betInfo;
}
