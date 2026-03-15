package com.cloud.baowang.play.api.vo.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CmdUpdateDataReq implements Serializable {
    // 用户名
    private String SourceName;
    // 当前交易金额变更余额
    private BigDecimal TransactionAmount;
    //注单号
    private String ReferenceNo;
    //本次执行提錢兑现的单号(仅ActionId: 3001使用)
    private String TransRefNo;
}
