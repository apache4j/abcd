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
public class ZfSessionBetReq {

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
     * 离线模式
     */
    @JsonProperty("offline")
    private boolean offline;

    /**
     * 时间戳
     */
    @JsonProperty("wagersTime")
    private long wagersTime;

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
     * 牌局唯一值
     */
    @JsonProperty("sessionId")
    private String sessionId;

    /**
     * 牌局唯一值
     */
    @JsonProperty("type")
    private String type;

    /**
     * 玩家账号唯一值
     */
    @JsonProperty("userId")
    private String userId;

    /**
     * 有效投注金额, 结算才会带入
     */
    @JsonProperty("turnover")
    private BigDecimal turnover;

    /**
     * 预扣金额
     */
    @JsonProperty("preserve")
    private BigDecimal preserve;

    /**
     * 玩家裝置資訊 (依營運商需求帶入; 營運商須在 Login/LoginWithoutRedirect帶入)
     */
    @JsonProperty("platform")
    private BigDecimal platform;

    /**
     * 牌局全部下注总和
     */
    @JsonProperty("sessionTotalBet")
    private BigDecimal sessionTotalBet;
    /**
     * 注单类型
     */
    @JsonProperty("statementType")
    private Integer statementType;
    /**
     * 游戏类型
     */
    @JsonProperty("gameCategory")
    private Integer gameCategory;
    /**
     * 仅用于免费游戏 (
     */
    @JsonProperty("freeSpinData")
    private ZfFreeSpinData freeSpinData;

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
