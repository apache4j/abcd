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
public class ZfBetReq {

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
     * 注单结账时间戳
     */
    @JsonProperty("wagersTime")
    private Long wagersTime;

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
     * 为 true 时表示此注单为离线开奖
     */
    @JsonProperty("isFreeRound")
    private Boolean isFreeRound;

    /**
     * 玩家账号唯一值 (依营运商需求带入; 离线开奖一定
     * 会带)
     */
    @JsonProperty("userId")
    private String userId;

    /**
     * 1. 鱼机游戏大单号 (依营运商需求带入)
     * 或 2. 离线开奖的触发局局号
     */
    @JsonProperty("transactionId")
    private String transactionId;

    /**
     * 玩家装置信息
     * (依营运商需求带入;
     * 营运商须在 Login /LoginWithoutRedirect带入)
     */
    @JsonProperty("platform")
    private String platform;

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
     * 仅用于免费游戏 (free spin)
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
                && wagersTime > 0
                && betAmount != null
                && winloseAmount != null;
    }

}
