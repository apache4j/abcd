package com.cloud.baowang.play.api.vo.sa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SAPlaceBetDetailsReq {

    /**
     * 类型
     */
    private Integer type;

    /**
     * 投注金额，最多两位小数
     */
    private BigDecimal amount;


}
