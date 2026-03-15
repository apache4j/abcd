package com.cloud.baowang.play.api.vo.cq9.request;

import cn.hutool.core.util.ObjectUtil;
//import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 入参
 * 投注与派彩/Rollout
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CQ9RollinReq {


    /**
     * 用户账户，最大长度为36个字符
     */
    private String account;

    /**
     * 事件时间，格式为 RFC3339 (如 2017-01-19T22:56:30-04:00)
     */
    private String eventTime;

    /**
     * 游戏厂商代号，最大长度为36个字符
     */
    private String gamehall;

    /**
     * 游戏代号，最大长度为36个字符
     */
    private String gamecode;

    /**
     * roundid 為唯一值
     * 注单号，最大长度为50个字符
     */
    private String roundid;

    /**
     * 有效投注，最多16位数字及小数点后4位
     */
    private BigDecimal validbet;

    /**
     * 下注金额，最多16位数字及小数点后4位
     */
    private BigDecimal bet;

    /**
     * 赢得金额，可为负值，最多16位数字及小数点后4位
     */
    private BigDecimal win;

    /**
     * 开房费用，仅适用于牌桌游戏，最多16位数字及小数点后4位
     */
    private BigDecimal roomfee;

    /**
     * 实际给玩家钱包的金额，不得为负值，最多16位数字及小数点后4位
     */
    private BigDecimal amount;

    /**
     * 交易代码，最大长度为70个字符
     */
    private String mtcode;

    /**
     * 成单时间，格式为 RFC3339 (如 2017-01-19T22:56:30-04:00)
     */
    private String createTime;

    /**
     * 抽水金额，仅适用于牌桌/真人游戏，最多16位数字及小数点后4位
     */
    private BigDecimal rake;

    /**
     * 游戏类型，可选值: 漁機遊戲(fish) / 牌桌遊戲(table) / 街機遊戲(arcade) / 真人遊戲(live)
     */
    private String gametype;

    private String wtoken;
    /**
     * 请求方式
     */
    private String callType;
    /**
     * 全部是其他扣除调整，OTHER_SUBTRACT("20", "其他扣除调整",BusinessCoinTypeEnum.OTHER_ADJUSTMENTS),
     * 投注与部分转账使用 GAME_BET("12", "投注",BusinessCoinTypeEnum.GAME_BET)
     * WalletEnum.CoinTypeEnum
     */
    private  String type;

    /**
     * 桌号，仅适用于真人游戏
     */
    private String tableid;

    /**
     * 收支类型1收入,2支出
     */
    private String balanceType;

    public Boolean valid() {
        if (!ObjectUtil.isAllNotEmpty(account, eventTime, gamehall, gamecode,
                roundid, amount, mtcode)) {
            return false;
        }
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isRFC3339TimeFormat(String timeStr) {
        if (timeStr == null || timeStr.isBlank()) {
            return false;
        }

        try {
            OffsetDateTime.parse(timeStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

}
