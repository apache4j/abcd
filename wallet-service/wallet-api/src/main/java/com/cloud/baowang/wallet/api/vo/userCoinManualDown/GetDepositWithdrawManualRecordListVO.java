package com.cloud.baowang.wallet.api.vo.userCoinManualDown;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author: kimi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "查询加额记录(会员存款(后台)) 和 减额记录(会员提款(后台))")
public class GetDepositWithdrawManualRecordListVO {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;

    @Schema(title = "开始时间")
    private Long start;

    @Schema(title = "结束时间")
    private Long end;

    @Schema(title = "代理账号")
    private String agentAccount;

    @Schema(title = "会员账号")
    private String userAccount;
}
