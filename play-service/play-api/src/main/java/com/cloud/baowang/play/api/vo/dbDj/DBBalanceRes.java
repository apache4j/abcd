package com.cloud.baowang.play.api.vo.dbDj;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DBBalanceRes {
    /**
     * 状态 (成功:true, 失败:false)
     */
    private String status;

    /**
     * 余额
     */
    private BigDecimal data;

    /**
     * 成功或失败信息
     */
    private String message;

}
