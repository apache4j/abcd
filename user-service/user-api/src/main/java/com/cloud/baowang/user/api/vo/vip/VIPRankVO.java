package com.cloud.baowang.user.api.vo.vip;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author 小智
 * @Date 2/5/23 4:58 PM
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "VIP等级配置返回对象")
public class VIPRankVO implements Serializable {

    @Schema(title = "id")
    private Long id;

    @Schema(title = "vip等级code")
    private Integer vipRankCode;

    @Schema(title = "vip等级")
    private String vipRank;

    @Schema(title = "vip等级名称")
    private String vipRankName;

    @Schema(title = "vipRankNameI18Code")
    private String vipRankNameI18nCode;

//    @ApiModelProperty("累计存款")
//    private BigDecimal depositUpgrade;

    @Schema(title = "累计有效流水")
    private BigDecimal betAmountUpgrade;

    @Schema(title = "保级流水")
    private BigDecimal relegationFlowUpgrade;

    @Schema(title = "保级有效期(天)")
    private Integer relegationDay;
}
