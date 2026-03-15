package com.cloud.baowang.play.api.vo.evo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * CancelRequest
 * 取消交易请求对象，用于撤销已发起的 DebitRequest 交易。
 * Cancel request object used to cancel a previously made DebitRequest transaction.
 */
@Data
public class CancelRequest implements Serializable {

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
     * 唯一请求 ID，用于标识一次 CancelRequest
     * Unique request Id, that identifies CancelRequest
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
         * 需要被取消的交易 ID
         * Will contain a transaction ID which needs to be canceled
         */
        private String id;

        /**
         * 用于关联 CancelRequest 到相应 DebitRequest 的引用 ID
         * Reference identifier linking CancelRequest to the appropriate DebitRequest
         */
        private String refId;

        /**
         * 被取消的借记交易金额（仅用于验证）
         * Amount of cancelled debit transaction in player's session currency (for validation only)
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
