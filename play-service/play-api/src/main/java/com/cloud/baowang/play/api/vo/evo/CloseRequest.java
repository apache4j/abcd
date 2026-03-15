package com.cloud.baowang.play.api.vo.evo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * CloseRequest
 * 游戏回合关闭请求
 * 用于通知运营方某个游戏回合已经结束
 */
@Data
public class CloseRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
     * 游戏信息
     * Game details
     */
    private Game game;

    /**
     * 唯一请求 ID
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
}
