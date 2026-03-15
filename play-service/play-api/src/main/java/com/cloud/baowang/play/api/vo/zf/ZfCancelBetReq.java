package com.cloud.baowang.play.api.vo.zf;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZfCancelBetReq {

    /**
     * 请求标识符。同一注单重送请求时会与前次不同。 (长
     * 度最长 50)
     */
    @JsonProperty("reqId")
    private String reqId;

    /**
     * 营运商 api access token
     */
    @JsonProperty("token")
    private String token;

    /**
     * 货币名称
     */
    @JsonProperty("currency")
    private String currency;

    /**
     * 游戏代码
     */
    @JsonProperty("game")
    private int game;

    /**
     * 注单唯一识别值
     */
    @JsonProperty("round")
    private String round;

    /**
     * 押注金额
     */
    @JsonProperty("betAmount")
    private BigDecimal betAmount;

    /**
     * 派彩金额
     */
    @JsonProperty("winloseAmount")
    private BigDecimal winloseAmount;



    /**
     * 玩家账号唯一值
     */
    @JsonProperty("userId")
    private String userId;

    private String platformCode;

    public boolean valid(){
        return StringUtils.isNotBlank(reqId)
                && StringUtils.isNotBlank(token)
                && StringUtils.isNotBlank(currency)
                && StringUtils.isNotBlank(round)
                && game > 0
                && betAmount != null
                && winloseAmount != null;
    }

}
