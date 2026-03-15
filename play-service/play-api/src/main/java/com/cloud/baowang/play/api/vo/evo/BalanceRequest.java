package com.cloud.baowang.play.api.vo.evo;

import lombok.Data;

import java.io.Serializable;

@Data
public class BalanceRequest implements Serializable {
    private String authToken;
    /** Player's session ID
     *  玩家会话ID
     */
    private String sid;

    /** Player's ID, assigned by Licensee
     *  玩家ID，由运营商分配
     */
    private String userId;

    /** Currency code (ISO 4217 3 letter code)
     *  货币代码（ISO 4217 三字母代码）
     */
    private String currency;

    /**
     * Object containing game details:
     * - In case of non-game related balance request (e.g user enters lobby) this object will be empty or null;
     * - Could be used to apply limits for specific game data, e.g limit by game.type.
     *
     * 游戏详情对象：
     * - 非游戏相关的余额请求（如用户进入大厅）该对象为空或null；
     * - 可用于对特定游戏数据应用限制，例如按game.type限制。
     */
    private Game game;

    /** Unique request ID, that identifies AAMSBalanceRequest
     *  唯一请求ID，用于标识该AAMSBalanceRequest
     */
    private String uuid;

    @Data
    public static class Game implements Serializable {
        /** The game type value (e.g. "blackjack", "roulette")
         *  游戏类型值（如 "blackjack", "roulette"）
         */
        private String type;

        /** Object containing additional game round details
         *  额外的游戏回合详情对象
         */
        private Details details;

        @Data
        public static class Details implements Serializable {
            /** Object containing table details for the game round
             *  本局游戏的桌台详情对象
             */
            private Table table;

            @Data
            public static class Table implements Serializable {
                /** Table identifier
                 *  桌台标识
                 */
                private String id;

                /**
                 * Virtual table identifier (the value may be null or empty
                 * if there is no virtual table ID present).
                 *
                 * 虚拟桌台标识（如果没有虚拟桌台ID，则该值可能为null或空字符串）
                 */
                private String vid;
            }
        }
    }
}
