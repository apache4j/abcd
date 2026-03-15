package com.cloud.baowang.play.wallet.vo.res.dg;

import com.cloud.baowang.play.wallet.enums.DGErrorEnum;
import com.cloud.baowang.play.wallet.vo.req.dg.Member;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DGBaseRsp {
    private int codeId;

    private Member member;


    public DGBaseRsp(int codeId, Member member) {
        this.codeId = codeId;
        this.member = member;
    }


    public static DGBaseRsp failed(DGErrorEnum resultCodeEnums, String userName) {
        return new DGBaseRsp(resultCodeEnums.getCode(), Member.builder().username(userName).build());
    }

    public static DGBaseRsp failed(DGErrorEnum resultCodeEnums, String userName, BigDecimal balance) {
        return new DGBaseRsp(resultCodeEnums.getCode(), Member.builder().username(userName).balance(balance).build());
    }


    public static DGBaseRsp success( BigDecimal balance,BigDecimal amount,String userName) {
        return new DGBaseRsp(DGErrorEnum.SUCCESS.getCode(), Member.builder().username(userName).balance(balance).amount(amount).build());
    }
    public static DGBaseRsp success( BigDecimal balance,String userName) {
        return new DGBaseRsp(DGErrorEnum.SUCCESS.getCode(), Member.builder().username(userName).balance(balance).build());
    }

    public static DGBaseRsp failed(DGErrorEnum resultCodeEnums) {
        return new DGBaseRsp(resultCodeEnums.getCode(),null);
    }

}
