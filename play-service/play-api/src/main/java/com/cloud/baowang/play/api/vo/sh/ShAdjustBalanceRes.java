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
public class ShAdjustBalanceRes implements Serializable {

  @Schema(title = "交易编号")
  private String transferNo;

  @Schema(title = "玩家账号")
  private String userName;

  @Schema(title = "实际交易金额(+ -)")
  private BigDecimal realAmount;

  @Schema(title = "用户余额")
  private BigDecimal currentAmount;

}
