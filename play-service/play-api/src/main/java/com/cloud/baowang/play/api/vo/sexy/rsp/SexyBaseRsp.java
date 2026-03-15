package com.cloud.baowang.play.api.vo.sexy.rsp;

import com.cloud.baowang.play.api.vo.sexy.enums.SexyErrorEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SexyBaseRsp {

    private String status;

    private String desc;

    private BigDecimal balance;

    private String balanceTs;

    public SexyBaseRsp(String status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public SexyBaseRsp(String status, String balanceTs, BigDecimal balance) {
        this.status = status;
        this.balanceTs = balanceTs;
        this.balance = balance;
    }

    public static SexyBaseRsp failed(SexyErrorEnum resultCodeEnums) {
        return new SexyBaseRsp(resultCodeEnums.getCode(), resultCodeEnums.getMessageEn());
    }

    public static SexyBaseRsp failed(SexyErrorEnum resultCodeEnums, BigDecimal balance) {
        return new SexyBaseRsp(resultCodeEnums.getCode(), resultCodeEnums.getMessageEn(),balance);
    }


    public static SexyBaseRsp success(BigDecimal balance) {
        return new SexyBaseRsp(SexyErrorEnum.SUCCESS.getCode(), SexyErrorEnum.SUCCESS.getMessageEn(), balance);
    }
    public static SexyBaseRsp success(BigDecimal balance,String balanceTs) {
        return new SexyBaseRsp(SexyErrorEnum.SUCCESS.getCode(),balanceTs, balance);
    }

    public static SexyBaseRsp success() {
        return new SexyBaseRsp(SexyErrorEnum.SUCCESS.getCode(), SexyErrorEnum.SUCCESS.getMessageEn());
    }
}
