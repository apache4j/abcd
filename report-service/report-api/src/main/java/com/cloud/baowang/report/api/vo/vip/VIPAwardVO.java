package com.cloud.baowang.report.api.vo.vip;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author : 小智
 * @Date : 2024/11/7 16:20
 * @Version : 1.0
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "用户VIP数据返回信息")
public class VIPAwardVO implements Serializable {

    @Schema(description = "vip等级code")
    private Integer vipGradeCode;

    @Schema(description = "已领取的红利")
    private BigDecimal receiveBonus;

    @Schema(title = "站点编码")
    private String siteCode;

}
