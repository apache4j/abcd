package com.cloud.baowang.play.api.vo.marbles;


import com.cloud.baowang.play.api.enums.marbles.MarblesRespErrEnums;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarblesResp {

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

    @JsonProperty("OperatorTransactionId")
    private String operatorTransactionId;

    @JsonProperty("TransactionId")
    private String transactionId;


    public static MarblesResp fail(MarblesRespErrEnums marblesRespErrEnums) {
        MarblesResp omgResp = new MarblesResp();
        omgResp.setCode(marblesRespErrEnums.getCode());
        omgResp.setMessage(marblesRespErrEnums.getDescription());
        return omgResp;
    }

    public static MarblesResp success(String playerID, String currency, String balance) {
        MarblesResp marblesResp = new MarblesResp();
        marblesResp.setCode(0);
        marblesResp.setMessage("Successful");
        marblesResp.setPlayerID(playerID);
        marblesResp.setCurrency(currency);
        marblesResp.setBalance(balance);
        return marblesResp;
    }


    public static MarblesResp success(String playerID, String currency, String balance,String operatorTransactionId) {
        MarblesResp marblesResp = new MarblesResp();
        marblesResp.setCode(0);
        marblesResp.setMessage("Successful");
        marblesResp.setPlayerID(playerID);
        marblesResp.setCurrency(currency);
        marblesResp.setBalance(balance);
        marblesResp.setOperatorTransactionId(operatorTransactionId);
        return marblesResp;
    }

    /**
     * @param operatorTransactionId 运营商交易代码。
     * @param transactionId  IM 交易代码。参考以下注意事项。
     */
    public static MarblesResp successPlaceBet(String balance,String operatorTransactionId,String transactionId) {
        MarblesResp marblesResp = new MarblesResp();
        marblesResp.setCode(0);
        marblesResp.setMessage("Successful");
        marblesResp.setBalance(balance);
        marblesResp.setOperatorTransactionId(operatorTransactionId);
        marblesResp.setTransactionId(transactionId);
        return marblesResp;
    }
}
