package com.cloud.baowang.play.api.vo.sba;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SBComboInfo {

    /** 唯一 id */
    private String refId;


    /** 例如：Parlay_Mix, Parlay_System, Parlay_Lucky, SingleBet_ViaLucky */
    private String parlayType;

    /** 注单金额 */
    private BigDecimal betAmount;

    /** 需增加在玩家的金额。 */
    private BigDecimal creditAmount;

    /** 需从玩家扣除的金额。 */
    private BigDecimal debitAmount;

    /**
     * SingleBet_ViaLucky – 请参阅表 Single Bet Detail.
     * Others –请参阅表 Parlay Detail.
     */
    private List<SBParlayDetailReq> detail;


}
