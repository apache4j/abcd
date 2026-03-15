package com.cloud.baowang.play.api.vo.sh;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShTransferOrderReq implements Serializable {

  @Schema(title = "交易类型")
  private String playType;

  @Schema(title = "注单号")
  private String orderNo;

  @Schema(title = "实际交易金额(+ -)")
  private BigDecimal amount;

}
