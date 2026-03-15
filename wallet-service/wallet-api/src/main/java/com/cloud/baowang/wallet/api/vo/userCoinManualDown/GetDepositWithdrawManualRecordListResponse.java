package com.cloud.baowang.wallet.api.vo.userCoinManualDown;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author: kimi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "查询加额记录(会员存款(后台)) 和 减额记录(会员提款(后台))")
public class GetDepositWithdrawManualRecordListResponse {

    @Schema(title = "会员存款(后台)金额")
    private Map<String, BigDecimal> depositAmount;

    @Schema(title = "会员提款(后台)金额")
    private Map<String, BigDecimal> withdrawAmount;
}
