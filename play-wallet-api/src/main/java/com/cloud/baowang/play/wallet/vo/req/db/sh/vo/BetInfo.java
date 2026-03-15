package com.cloud.baowang.play.wallet.vo.req.db.sh.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;

@Data
public class BetInfo {
    private BigDecimal betAmount;
    private Integer betPointId;
    private Long betId;

    public static class BetAmountComparator implements Comparator<BetInfo> {
        @Override
        public int compare(BetInfo o1, BetInfo o2) {
            return o1.getBetAmount().compareTo(o2.getBetAmount());
        }
    }
}
