package com.cloud.baowang.wallet.api.vo.fundadjust;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: kimi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "查询 Request")
public class GetUserBalanceQueryVO {

    @Schema(description = "siteCode", hidden = true)
    private String siteCode;

    @Schema(title = "会员ID")
    @NotBlank(message = "会员账号不能为空")
    private String userAccount;
}
