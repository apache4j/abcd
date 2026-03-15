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
public class TxnsReq {

    @Schema(title = "唯一")
    private String refId;

    @Schema(title = "需增加在玩家的金额。")
    private BigDecimal creditAmount;

    @Schema(title = "需从玩家扣除的金额。")
    private BigDecimal debitAmount;

}