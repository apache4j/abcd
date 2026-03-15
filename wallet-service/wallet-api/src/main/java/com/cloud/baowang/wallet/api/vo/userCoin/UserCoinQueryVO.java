package com.cloud.baowang.wallet.api.vo.userCoin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiqi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description ="会员钱包查询请求对象")
public class UserCoinQueryVO {
    /**
     * 会员账号
     */
    @Schema(description = "会员账号",hidden = true)
    private String userAccount;

    @Schema(description = "会员id",hidden = true)
    private String userId;

    @Schema(description = "站点code",hidden = true)
    private String siteCode;

    private String currencyCode;

}
