package com.cloud.baowang.play.api.vo.sh;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShBalanceRes implements Serializable {

  @Schema(title = "玩家账号")
  private String userName;

  @Schema(title = "用户币种")
  private String currencyCode;

  @Schema(title = "用户余额")
  private BigDecimal balanceAmount;

  @Schema(title = "玩家记录的更新时间 Unix 时间戳，以毫秒为单位)")
  private Long updatedTime;

}
