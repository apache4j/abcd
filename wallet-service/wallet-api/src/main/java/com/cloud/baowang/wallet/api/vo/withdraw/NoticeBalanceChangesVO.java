package com.cloud.baowang.wallet.api.vo.withdraw;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeBalanceChangesVO {
    /**
     * 余额变动通知
     */
    private BigDecimal balance;


}
