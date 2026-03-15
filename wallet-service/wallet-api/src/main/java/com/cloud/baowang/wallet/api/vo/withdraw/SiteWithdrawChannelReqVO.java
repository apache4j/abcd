package com.cloud.baowang.wallet.api.vo.withdraw;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author : ford
 * @Date : 2024/10/28 15:49
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="提款通道请求参数")
public class SiteWithdrawChannelReqVO  {

    @Schema(description = "站点code")
    private String siteCode;

    @Schema(description = "提款类型编码")
    private String withdrawTypeCode;

}
