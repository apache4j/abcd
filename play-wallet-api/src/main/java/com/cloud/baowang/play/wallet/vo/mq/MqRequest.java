package com.cloud.baowang.play.wallet.vo.mq;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/05/31 15:33
 * @description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MqRequest {
    @Schema(title = "用户id")
    private String userAccount;
    @Schema(title = "用户类型")
    private Integer accountType;
    @Schema(title = "订单id")
    private String orderId;
    @Schema(title = "有效投注")
    private BigDecimal validAmount;
    /** {@link com.cloud.baowang.play.api.enums.ClassifyEnum}*/
    @Schema(title = "订单状态归类")
    private Integer orderClassify;
}
