package com.cloud.baowang.play.game.evo.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Evolution 真人游戏的注单详情响应
 */
@Data
public class EvoBetRecordRes {

    /**
     * 注单唯一 ID
     */
    private String id;

    /**
     * 游戏提供商（如 evolution、pragmatic 等）
     */
    private String gameProvider;

    /**
     * 游戏开始时间（UTC ISO 8601 格式）
     */
    private Instant startedAt;

    /**
     * 游戏结算时间（UTC ISO 8601 格式）
     */
    private Instant settledAt;

    /**
     * 注单状态：
     * Resolved：已结算
     * Pending：待结算
     */
    private String status;

    /**
     * 游戏类型（如 roulette 轮盘、baccarat 百家乐）
     */
    private String gameType;

    /**
     * 游戏桌台信息
     */
    private Table table;

    /**
     * 发牌员 / 主持人信息
     */
    private Dealer dealer;

    /**
     * 注单货币（ISO 4217 三位字母代码，如 EUR、USD、CNY）
     */
    private String currency;

    /**
     * 总投注额（单位同 currency）
     */
    private BigDecimal wager;

    /**
     * 总派彩金额（单位同 currency）
     */
    private BigDecimal payout;

    /**
     * 参与本局游戏的玩家信息列表
     */
    private List<Participant> participants;

    /**
     * 游戏开奖结果信息
     */
    private Result result;

    /**
     * 桌台信息
     */
    @Data
    public static class Table {
        /**
         * 桌台 ID
         */
        private String id;

        /**
         * 桌台名称（如 Roulette VIP）
         */
        private String name;
    }

    /**
     * 发牌员 / 主持人信息
     */
    @Data
    public static class Dealer {
        /**
         * 发牌员唯一 ID
         */
        private String uid;

        /**
         * 发牌员姓名
         */
        private String name;
    }

    /**
     * 玩家参与信息
     */
    @Data
    public static class Participant {

        /**
         * 赌场 ID（运营商唯一标识）
         */
        private String casinoId;

        /**
         * 玩家 ID
         */
        private String playerId;

        /**
         * 玩家显示昵称
         */
        private String screenName;

        /**
         * 玩家在游戏商的会话 ID
         */
        private String sessionId;

        /**
         * 玩家在赌场的会话 ID
         */
        private String casinoSessionId;

        /**
         * 游戏渠道：
         * desktop：桌面端
         * mobile：移动端
         */
        private String channel;

        /**
         * 玩家下注使用的货币
         */
        private String currency;

        /**
         * 玩家下注记录列表
         */
        private List<Bet> bets;

        /**
         * 配置覆盖项（如虚拟桌台 ID）
         */
        private List<String> configOverlays;

        private String status;

        /**
         * 单笔下注记录
         */
        @Data
        public static class Bet {
            /**
             * 下注代码（如 ROU_1Red）
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
             * 下单时间（UTC ISO 8601 格式）
             */
            private String placedOn;

            /**
             * 下注描述（如 1 Red、32 Red）
             */
            private String description;

            /**
             * 交易 ID（关联账变记录）
             */
            private String transactionId;
        }
    }

    /**
     * 游戏结果
     */
    @Data
    public static class Result {

        /**
         * 开奖结果列表（可能有多个，如骰子游戏）
         */
        private List<Outcome> outcomes;

        /**
         * 单个开奖结果
         */
        @Data
        public static class Outcome {
            /**
             * 开奖号码（如 "32"）
             */
            private String number;

            /**
             * 类型：
             * Even：偶数
             * Odd：奇数
             */
            private String type;

            /**
             * 颜色：
             * Red：红色
             * Black：黑色
             */
            private String color;
        }
    }
}
