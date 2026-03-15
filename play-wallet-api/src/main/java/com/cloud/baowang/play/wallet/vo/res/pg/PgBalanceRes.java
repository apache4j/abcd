package com.cloud.baowang.play.wallet.vo.res.pg;

import com.cloud.baowang.common.core.serializer.BigDecimalTwoDecimalSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PgBalanceRes implements Serializable {

  @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
  @Schema(title = "用户余额")
  private BigDecimal balance_amount;

  @Schema(title = "用户币种")
  private String currency_code ;

  @Schema(title = "玩家记录的更新时间 Unix 时间戳，以毫秒为单位)")
  private Long updated_time;

}
