package com.cloud.baowang.user.api.vo.vip;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author : 小智
 * @Date : 12/6/24 11:48 AM
 * @Version : 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "VIP返水返回对象")
public class VIPRebateVO {

    @Schema(title = "VIP等级")
    private Integer vipRankCode;

    @Schema(title = "VIP等级下返水需要的最低下注金额")
    private BigDecimal minBetAmount;

    @Schema(title = "VIP等级下返水比例")
    private BigDecimal rebateProportion;
}
