package com.cloud.baowang.wallet.api.vo.userCoinRecord;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(title = "批量归集")
public class BatchCollectVO {

    @Schema(description = "链类型")
    private String chainType;

    @Schema(description = "归集最小金额")
    private BigDecimal collectMinAmount;

    @Schema(description = "站点编码")
    private String platNo;

    @Schema(description = "归属用户类型 USER AGENT")
    private String ownerUserType;
}
