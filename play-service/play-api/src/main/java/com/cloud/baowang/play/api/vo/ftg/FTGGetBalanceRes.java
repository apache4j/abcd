package com.cloud.baowang.play.api.vo.ftg;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FTGGetBalanceRes {


    /**
     * 使用者唯一标识符 (uid)，长度为 3 到 20 字符。
     */
    private String uid;

    /**
     * 货币类型，代表币种的代号。
     */
    private Integer currency;

    /**
     * 请求的 UUID，标准 16 字节 UUID。
     * 在调用运营商 API 时携带，运营商需返回相同的值。
     */
    private String request_uuid;

    /**
     * 用户当前的账户余额，支持整数部分最大为 12 位，小数部分最多 4 位。
     * 最大值为 999,999,999,999.9999。
     */
    private BigDecimal balance;

}
