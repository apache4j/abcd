package com.cloud.baowang.play.game.pg.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Objects;

@Data
public class PgBetRecordRes {

    /**
     * 母注单的唯一标识符
     */
    private Long parentBetId;
    /**
     * 子投注的唯一标识符 （唯一键值）
     */
    private String betId;
    /**
     * 玩家的唯一标识符
     */
    private String playerName;
    /**
     * 游戏的唯一标识符
     */
    private Integer gameId;
    /**
     * 投注记录的类别: 1: 真实游戏
     */
    private Integer betType;
    /**
     * 交易类别： 0：调整后的投注（重置游戏状态） 1: 现金 2: 红利 3: 免费游戏
     */
    private Integer transactionType;
    /**
     * 投注记录平台
     */
    private Integer platform;
    /**
     * 记录货币
     */
    private String currency;
    /**
     * 玩家的投注额
     */
    private BigDecimal betAmount;
    /**
     * 玩家的所赢金额
     */
    private BigDecimal winAmount;
    /**
     * 玩家的奖池返还率贡献额
     */
    private BigDecimal jackpotRtpContributionAmount;
    /**
     * 玩家的奖池贡献额
     */
    private BigDecimal jackpotContributionAmount;
    /**
     * 玩家的奖池金额
     */
    private BigDecimal jackpotWinAmount;
    /**
     * 玩家交易前的余额
     */
    private BigDecimal balanceBefore;
    /**
     * 玩家交易后的余额
     */
    private BigDecimal balanceAfter;
    /**
     * 投注状态： 1: 非最后一手投注 2：最后一手投注 3：已调整
     */
    private Integer handsStatus;
    /**
     * 数据更新时间
     */
    private Long rowVersion;
    /**
     * 当前投注的开始时间
     */
    private Long betTime;
    /**
     * 当前投注的结束时间
     */
    private Long betEndTime;
    /**
     * 表示旋转类型： True：特色旋转 False：普通旋转
     */
    private Boolean isFeatureBuy;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PgBetRecordRes that = (PgBetRecordRes) o;
        return Objects.equals(betId, that.betId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(betId);
    }
}
