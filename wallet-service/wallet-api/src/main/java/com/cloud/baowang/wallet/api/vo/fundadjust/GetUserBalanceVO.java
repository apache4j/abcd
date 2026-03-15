package com.cloud.baowang.wallet.api.vo.fundadjust;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: kimi
 */
@Data
@Schema(title = "查询 返回")
public class GetUserBalanceVO {

    @Schema(title = "会员账号s 分号拼接")
    private String userAccounts;

    @Schema(description = "货币")
    private String currencyCode;

    @Schema(title = "会员ids 分号拼接",hidden = true)
    private String userIds;

}
