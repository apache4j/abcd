package com.cloud.baowang.play.api.vo.marbles;


import com.cloud.baowang.play.api.enums.marbles.MarblesRespErrEnums;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarblesBalanceResp {

    @JsonProperty("Code")
    private int code;

    @JsonProperty("Message")
    private String message;

    @JsonProperty("PlayerID")
    private String playerID;

    @JsonProperty("Currency")
    private String currency;

    @JsonProperty("Balance")
    private String balance;


    public static MarblesBalanceResp fail(MarblesRespErrEnums marblesRespErrEnums) {
        MarblesBalanceResp omgResp = new MarblesBalanceResp();
        omgResp.setCode(marblesRespErrEnums.getCode());
        omgResp.setMessage(marblesRespErrEnums.getDescription());
        return omgResp;
    }

    public static MarblesBalanceResp success(String playerID, String currency, String balance) {
        MarblesBalanceResp marblesResp = new MarblesBalanceResp();
        marblesResp.setCode(0);
        marblesResp.setMessage("Successful");
        marblesResp.setPlayerID(playerID);
        marblesResp.setCurrency(currency);
        marblesResp.setBalance(balance);
        return marblesResp;
    }

}
