package com.cloud.baowang.play.api.vo.evo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * PromoPayoutRequest
 * Promotional payout transaction request.
 * 用于传输促销派彩相关的交易信息。
 */
@Data
public class PromoPayoutRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private String authToken;

    /**
     * 玩家会话 ID（SID）
     * Player’s session ID
     */
    private String sid;

    /**
     * 玩家 ID（由运营商分配）
     * Player's ID, assigned by Licensee
     */
    private String userId;

    /**
     * 玩家会话币种（ISO 4217 三位字母代码）
     * Currency code (ISO 4217 3-letter code) of player's session currency
     */
    private String currency;

    /**
     * 游戏信息（可选，仅当促销派彩发生在特定游戏回合中才存在）
     * Game details (optional)
     */
    private Game game;

    /**
     * 促销交易信息
     * Promotional transaction details
     */
    private PromoTransaction promoTransaction;

    /**
     * 唯一请求 ID，用于标识此次 PromoPayoutRequest
     * Unique request Id
     */
    private String uuid;

    // ==================== 内部类 ====================

    @Data
    public static class Game implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 唯一游戏回合 ID
         * Unique game round ID
         */
        private String id;

        /**
         * 游戏类型
         * The game type value (e.g. "blackjack", "roulette")
         */
        private String type;

        /**
         * 游戏附加详情
         */
        private Details details;

        @Data
        public static class Details implements Serializable {
            @Serial
            private static final long serialVersionUID = 1L;

            /**
             * 桌台信息
             */
            private Table table;

            @Data
            public static class Table implements Serializable {
                @Serial
                private static final long serialVersionUID = 1L;

                /**
                 * 桌台 ID
                 */
                private String id;

                /**
                 * 虚拟桌台 ID（可能为 null 或空字符串）
                 */
                private String vid;
            }
        }
    }

    @Data
    public static class PromoTransaction implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 促销交易类型
         * Type of promo transaction
         */
        private String type;

        /**
         * 券初始化/来源信息
         */
        private Origin origin;

        /**
         * 促销交易唯一 ID
         */
        private String id;

        /**
         * 促销交易金额
         */
        private BigDecimal amount;

        /**
         * 免费局券 ID（适用于 FreeRoundPlayableSpent 或 Reward Games）
         */
        private String voucherId;

        /**
         * 剩余免费局数
         */
        private Integer remainingRounds;

        /**
         * 所有中奖彩金信息（仅 JackpotWin 类型时存在）
         */
        private List<Jackpot> jackpots;

        /**
         * 剩余可玩余额（Reward Games 相关）
         */
        private BigDecimal playableBalance;

        /**
         * 奖金配置 ID（RtrMonetaryReward）
         */
        private String bonusConfigId;

        /**
         * 奖励 ID（RtrMonetaryReward）
         */
        private String rewardId;

        /**
         * 奖金代码（SmartTournamentMonetaryReward 或 SmartSpinsMonetaryReward）
         */
        private String instanceCode;

        /**
         * RedTiger Smart Spins 系统内部奖金 ID（可为 null）
         */
        private Integer instanceId;

        /**
         * 活动代码（SmartTournamentMonetaryReward 或 SmartSpinsMonetaryReward）
         */
        private String campaignCode;

        /**
         * Red Tiger promo 系统内部活动 ID（可为 null）
         */
        private String campaignId;

        /**
         * Livespins promo 系统内部活动 ID（CashReward）
         */
        private String campaignIdString;

        /**
         * 促销派彩原因（CashReward）
         */
        private String reason;

        @Data
        public static class Origin implements Serializable {
            @Serial
            private static final long serialVersionUID = 1L;

            /**
             * 券来源类型（当前仅支持 SpinGifts）
             */
            private String type;
        }

        @Data
        public static class Jackpot implements Serializable {
            @Serial
            private static final long serialVersionUID = 1L;

            /**
             * 中奖彩金 ID
             */
            private String id;

            /**
             * 中奖金额（保留 6 位小数）
             */
            private BigDecimal winAmount;
        }
    }
}
