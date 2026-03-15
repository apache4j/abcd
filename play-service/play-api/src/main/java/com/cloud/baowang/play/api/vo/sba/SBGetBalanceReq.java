package com.cloud.baowang.play.api.vo.sba;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SBGetBalanceReq implements Serializable {

  @Schema(title = "GetBalance")
  private String action;

  @Schema(title = " 用户 id")
  private String userId;
}
