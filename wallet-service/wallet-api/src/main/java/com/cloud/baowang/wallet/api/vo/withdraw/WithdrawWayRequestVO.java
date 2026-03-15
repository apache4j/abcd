package com.cloud.baowang.wallet.api.vo.withdraw;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WithdrawWayRequestVO {


    private String mainCurrency;

    private Integer vipRank;

    private String siteCode;
}
