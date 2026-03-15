package com.cloud.baowang.activity.api.vo.redbag;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Schema(description = "红包雨发送请求信息")
@AllArgsConstructor
@NoArgsConstructor
public class RedBagSendRespVO {
    @Schema(description = "红包金额")
    private BigDecimal redbagAmount;
}
