package com.cloud.baowang.play.wallet.vo.req.db.sh.vo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BetRspParams {
    private BigDecimal realAmount;
    private BigDecimal badAmount;
    private BigDecimal rollbackAmount;
    private BigDecimal balance;
    private String loginName;
    private Long transferNo;
    private BigDecimal realBetAmount;
    private List<BetInfo> realBetInfo;

    private String merchantCode;
}
