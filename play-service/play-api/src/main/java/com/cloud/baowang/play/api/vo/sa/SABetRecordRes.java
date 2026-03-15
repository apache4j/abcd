package com.cloud.baowang.play.api.vo.sa;


import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SABetRecordRes {

    /**
     * 投注时间
     */
//    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime BetTime;

    /**
     * 结算时间
     */
//    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime PayoutTime;

    /**
     * 用户名 (最长48字符)
     */
    private String Username;

    /**
     * 桌台ID
     */
    private Integer HostID;

    /**
     * 保留字段
     */
    private String Detail;

    /**
     * 游戏编号 (最长64字符)
     */
    private String GameID;

    /**
     * 局
     */
    private Integer Round;

    /**
     * 靴
     */
    private Integer Set;

    /**
     * 投注编号 (64位整数)
     */
    private Long BetID;

    /**
     * 币种
     */
    private String Currency;

    /**
     * 投注金额
     */
    private BigDecimal BetAmount;

    /**
     * 有效投注额/洗码量
     */
    private BigDecimal Rolling;

    /**
     * 输赢金额
     */
    private BigDecimal ResultAmount;

    /**
     * 投注后的余额（不适用于单一钱包）
     */
    private BigDecimal Balance;

    /**
     * 游戏类型
     */
    private String GameType;

    /**
     * 真人游戏: 不同的投注类型
     */
    private Integer BetType;

    /**
     * 投注来源（设备平台代码）
     */
    private Integer BetSource;

    /**
     * 单一钱包下注交易编号，如非单一钱包接口为 -1
     */
    private Long TransactionID;

    /**
     * 投注确认方式（0: 手动, 1: 自动）
     */
    private Integer BetConfirmation;

    /**
     * 游戏结果（XML格式）
     */
    private JSONObject GameResult;

}
