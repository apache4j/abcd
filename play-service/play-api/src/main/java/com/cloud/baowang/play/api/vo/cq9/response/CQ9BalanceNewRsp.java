/*
package com.cloud.baowang.play.api.vo.cq9.response;

import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

*/
/**
 * CQ9基础响应结构类
 *
 * @author: lavine
 * @creat: 2023/9/11 11:50
 *//*

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CQ9BalanceNewRsp {
    @Schema(title = "币种")
    private String currency;

    */
/**
     * 余额
     * ※須支援12+4(小數至少第4位)
     *//*

    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal balance;
    */
/**
     *  扣除之前的金额
     *//*

    */
/*@JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal amount;*//*


}

*/
