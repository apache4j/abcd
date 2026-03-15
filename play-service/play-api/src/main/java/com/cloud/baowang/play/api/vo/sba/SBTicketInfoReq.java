package com.cloud.baowang.play.api.vo.sba;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SBTicketInfoReq {

    @Schema(title = "ConfirmBet")
    private String refId;

    @Schema(title = "沙巴体育系统交易 id")
    private String txId;

    @Schema(title = "商户系统交易 id")
    private String licenseeTxId;

    @Schema(title = "例如：-0.75, 0.96")
    private BigDecimal odds;

    //SBOddsTypeEnum枚举
    @Schema(title = " 适用于 bettype = 8700(Player Tips)才会有值")
    private Integer oddsType;

    @Schema(title = "实际注单金额")
    private BigDecimal actualAmount;

    @Schema(title = "例如：true, false")
    private boolean isOddsChanged;

    @Schema(title = "需增加在玩家的金额。")
    private BigDecimal creditAmount;

    @Schema(title = "需从玩家扣除的金额。")
    private BigDecimal debitAmount;

    @Schema(title = "决胜时间(仅显示日期)")
    private String winlostDate;

    @Schema(title = "为 MMR 赔率类型提供的赔率 %。当 oddsType=6 时,才返回。")
    private BigDecimal mmrPercentage;

    @Schema(title = "N (boolean) MMR 赔率的%是否改变。例如：true, false")
    private boolean isMmrPercentageChange;



}
