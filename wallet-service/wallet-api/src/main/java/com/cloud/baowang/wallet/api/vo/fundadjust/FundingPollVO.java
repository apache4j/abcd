package com.cloud.baowang.wallet.api.vo.fundadjust;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: kimi
 */
@Data
@Schema(title ="资金审核轮询 返回")
public class FundingPollVO {

    @Schema(title ="资金审核页面名称")
    private String pageName;

    @Schema(title ="新订单数量")
    private Integer number;
}
