package com.cloud.baowang.play.game.evo.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Evolution 游戏记录 DTO
 */
@Data
public class EvoGameRecordDTO {

    private String json;

    /**
     * 游戏记录唯一 ID
     */
    private String id;

    /**
     * 游戏提供商
     */
    private String gameProvider;

    /**
     * 游戏子提供商
     */
    private String gameSubProvider;

    /**
     * 游戏开始时间 (UTC ISO8601 格式)
     */
    private String startedAt;

    /**
     * 游戏结算时间 (UTC ISO8601 格式)
     */
    private String settledAt;

    /**
     * 游戏状态，例如: Resolved
     */
    private String status;

    /**
     * 游戏类型，例如: blackjack 21 点
     */
    private String gameType;

    /**
     * 桌子信息
     */
    private Table table;

    /**
     * 庄家信息
     */
    private Dealer dealer;

    /**
     * 游戏主货币 (例如 EUR)
     */
    private String currency;

    /**
     * 参与的玩家列表
     */
    private List<Participant> participants;

    /**
     * 游戏结果
     */
    private Result result;

    /**
     * 总下注金额 (可能带汇率换算)
     */
    private Double wager;

    /**
     * 总派彩金额
     */
    private Double payout;

    @Data
    public static class Table {
        /**
         * 桌子 ID
         */
        private String id;

        /**
         * 桌子名称
         */
        private String name;
    }

    @Data
    public static class Dealer {
        /**
         * 荷官 UID
         */
        private String uid;

        /**
         * 荷官昵称
         */
        private String name;
    }

    @Data
    public static class Participant {
        /**
         * 结果相关链接 (通常是渲染用的 HTML)
         */
        private ResultLink result;

        /**
         * 所属赌场 ID
         */
        private String casinoId;

        /**
         * 玩家 ID
         */
        private String playerId;

        /**
         * 玩家昵称
         */
        private String screenName;

        /**
         * 玩家游戏 ID (记录与玩家绑定)
         */
        private String playerGameId;

        /**
         * 玩家会话 ID
         */
        private String sessionId;

        /**
         * 赌场会话 ID
         */
        private String casinoSessionId;

        /**
         * 玩家下注币种 (例如 CNY)
         */
        private String currency;

        /**
         * 玩家下注详情列表
         */
        private List<Bet> bets;

        /**
         * 配置覆盖 (一般为空数组)
         */
        private List<Object> configOverlays;

        /**
         * 游戏模式，例如 RealMoney
         */
        private String playMode;

        /**
         * 渠道，例如 desktop/mobile
         */
        private String channel;

        /**
         * 操作系统，例如 macOS
         */
        private String os;

        /**
         * 设备类型，例如 Desktop
         */
        private String device;

        /**
         * 汇率版本号
         */
        private String currencyRateVersion;

        /**
         * 玩家状态，例如 Resolved
         */
        private String status;

        /**
         * 座位配置 (键是 Seat2, Seat3 等)
         */
        private Map<String, SeatConfig> seats;

        @Data
        public static class ResultLink {
            /**
             * 渲染结果链接
             */
            private String link;
        }

        @Data
        public static class Bet {
            /**
             * 下注位置/类型
             */
            private String code;

            /**
             * 下注金额
             */
            private BigDecimal stake;

            /**
             * 派彩金额
             */
            private BigDecimal payout;

            /**
             * 下注时间 (UTC ISO8601)
             */
            private String placedOn;

            /**
             * 交易 ID
             */
            private String transactionId;
        }

        @Data
        public static class SeatConfig {
            /**
             * 是否保险
             */
            private boolean insurance;

            /**
             * 是否双倍下注
             */
            private boolean doubleDown;

            /**
             * 是否分牌
             */
            private boolean splitHand;

            /**
             * 是否跟注（下注别人座位）
             */
            private boolean betBehind;

            /**
             * 是否买到 18 点
             */
            private boolean buyTo18;
        }
    }

    @Data
    public static class Result {
        /**
         * 庄家结果
         */
        private DealerResult dealer;

        /**
         * 各座位结果 (键是 Seat2, Seat3 等)
         */
        private Map<String, SeatResult> seats;

        /**
         * 烧掉的牌 (未发出的牌)
         */
        private List<String> burnedCards;

        @Data
        public static class DealerResult {
            /**
             * 庄家点数
             */
            private int score;

            /**
             * 庄家手牌
             */
            private List<String> cards;

            /**
             * 庄家奖励牌
             */
            private List<String> bonusCards;

            /**
             * 是否 Blackjack
             */
            private boolean isBlackjack;
        }

        @Data
        public static class SeatResult {
            /**
             * 玩家决策记录
             */
            private List<Decision> decisions;

            /**
             * 玩家点数
             */
            private int score;

            /**
             * 结果 (例如: EarlyCashOut)
             */
            private String outcome;

            /**
             * 奖励牌
             */
            private List<String> bonusCards;

            /**
             * 手牌
             */
            private List<String> cards;

            @Data
            public static class Decision {
                /**
                 * 决策时间
                 */
                private String recordedAt;

                /**
                 * 决策类型 (例如 EarlyCashOut)
                 */
                private String type;
            }
        }
    }
}
