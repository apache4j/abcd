package com.cloud.baowang.play.api.vo.evo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * DebitRequest
 * 借记请求对象，用于记录玩家游戏中扣款的信息
 * Debit request object used to record player debit transactions in games.
 */
@Data
public class DebitRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private String authToken;
    /**
     * 玩家会话 ID（SID）
     * Player’s session ID (SID)
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
     * 游戏信息对象
     * Object containing game details
     */
    private Game game;

    /**
     * 交易信息对象
     * Object containing transaction details
     */
    private Transaction transaction;

    /**
     * 唯一请求 ID，用于标识一次 DebitRequest
     * Unique request Id, that identifies DebitRequest
     */
    private String uuid;

    // ==================== 内部类 ====================

    @Data
    public static class Game implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 唯一的游戏回合 ID（仅在 Debit/Credit/Cancel/Close 请求中提供）
         * Unique game round id (only provided with Debit/Credit/Cancel/Close requests)
         */
        private String id;

        /**
         * 游戏类型（例如 blackjack、roulette）
         * The game type value (e.g. "blackjack", "roulette")
         */
        private String type;

        /**
         * 游戏的附加详情
         * Object containing additional game round details
         */
        private Details details;

        @Data
        public static class Details implements Serializable {
            @Serial
            private static final long serialVersionUID = 1L;

            /**
             * 桌台详情
             * Object containing table details for the game
             */
            private Table table;

            @Data
            public static class Table implements Serializable {
                @Serial
                private static final long serialVersionUID = 1L;

                /**
                 * 桌台 ID
                 * Table identifier
                 */
                private String id;

                /**
                 * 虚拟桌台 ID（可能为 null 或空字符串）
                 * Virtual table identifier (may be null or empty if not present)
                 */
                private String vid;
            }
        }
    }

    @Data
    public static class Transaction implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 交易唯一标识（防止重复交易并支持验证）
         * The unique identifier of a transaction
         */
        private String id;

        /**
         * 交易引用 ID，用于将 Credit 或 Cancel 请求关联到正确的 Debit 请求
         * Reference identifier linking Credit/Cancel requests to the appropriate Debit request
         */
        private String refId;

        /**
         * 扣款金额（按运营商设置进行四舍五入）
         * Amount of debit transaction in player's session currency
         */
        private BigDecimal amount;

        /**
         * 投注详情（可选/可配置，需要运营商启用）
         * Object containing bet details (optional/configurable)
         */
        private List<Bets> bets;

        @Data
        public static class Bets implements Serializable {
            @Serial
            private static final long serialVersionUID = 1L;

            /**
             * 投注代码
             * Bet code
             */
            private String code;

            /**
             * 投注金额
             * Bet amount
             */
            private BigDecimal amount;
        }
    }
}
