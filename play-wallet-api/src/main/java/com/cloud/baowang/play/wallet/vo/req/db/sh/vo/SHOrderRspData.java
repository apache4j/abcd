package com.cloud.baowang.play.wallet.vo.req.db.sh.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SHOrderRspData {

    //状态
    private String loginName;
    //账变后玩家余额
    private BigDecimal balance;
}

