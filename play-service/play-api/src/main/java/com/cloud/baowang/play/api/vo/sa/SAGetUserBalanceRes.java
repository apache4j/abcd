package com.cloud.baowang.play.api.vo.sa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SAGetUserBalanceRes {

    /**
     * 用户名，最长48字符，必填
     */
    private String username;

    /**
     * 货币单位（标准 ISO 3 字符，如 USD、EUR，或特例如 mXBT），最长16字符，必填
     */
    private String currency;

    /**
     * 金额（Decimal，最多2位小数），必填
     */
    private BigDecimal amount;

    /**
     * 错误代码，必填，对应 ErrorCode 枚举
     */
    private Integer error;

}
