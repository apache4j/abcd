package com.cloud.baowang.play.wallet.vo.res.bti;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BtiRefreshRsp implements Serializable {
    private String status;
    private BigDecimal balance;

    public static BtiRefreshRsp success(BigDecimal balance){
        BtiRefreshRsp rsp=new BtiRefreshRsp();
        rsp.setStatus("success");
        balance.setScale(2, RoundingMode.DOWN);
        rsp.setBalance(balance);
        return rsp;
    }

    public static BtiRefreshRsp err(){
        BtiRefreshRsp rsp=new BtiRefreshRsp();
        rsp.setStatus("failure");
        rsp.setBalance(BigDecimal.ZERO);
        return rsp;
    }

}
