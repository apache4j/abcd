package com.cloud.baowang.play.wallet.vo.req.sa;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class SaGetUserBalance {

    @Schema(title = "用户名")
    private String username;

    @Schema(title = "币种")
    private String currency;


}
