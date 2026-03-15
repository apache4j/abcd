package com.cloud.baowang.common.kafka.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "勋章-注单有效投注统计信息")
@Data
public class MedalValidOrderMqVO {

    @Schema(description = "会员id")
    private String userId;


    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "有效投注")
    private BigDecimal validAmount;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "站点代码")
    private String siteCode;




}
