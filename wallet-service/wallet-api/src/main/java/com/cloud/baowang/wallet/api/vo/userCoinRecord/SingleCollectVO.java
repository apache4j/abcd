package com.cloud.baowang.wallet.api.vo.userCoinRecord;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "单个归集")
public class SingleCollectVO {

    @Schema(description = "链类型",hidden = true)
    private String chainType;

    @Schema(description = "地址")
    private String addressNo;

    @Schema(description = "站点编码")
    private String platNo;
}
