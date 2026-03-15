package com.cloud.baowang.play.api.vo.sba;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class /**/SBParlayDetailReq {


    /**
     * 例如：0, 1, 2 (附录: 串关类型)
     * SBComboTypeEnum
     */
    private int type;

    /**
     * 例如：Treble (1 Bet), Trixie (4 Bets) (附录: 串关类型)
     */
    private String name;

    /**
     * 例如：1, 3, 4
     */
    private int betCount;

    /**
     * 输入注单金额
     */
    private BigDecimal stake;

    /**
     * 只在 Parlay_Mix 中存在
     */
    private BigDecimal odds;

    private String matchId;


}
