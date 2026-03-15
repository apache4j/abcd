package com.cloud.baowang.play.api.vo.evo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * CheckUserRequest
 * 请求对象，用于检查用户信息。
 * Request object for checking user information.
 */
@Data
public class CheckUserRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String authToken;

    /**
     * 玩家 ID（在 UserAuthentication 调用中由运营商发送的 player.id）
     * Player’s ID which is sent by Licensee in UserAuthentication call (player.id)
     */
    private String userId;

    /**
     * 玩家会话 ID（SID）
     * Player’s session ID (SID)
     * @see <a href="SID overview">SID overview</a>
     */
    private String sid;

    /**
     * 渠道信息对象，包含玩家设备类型等信息
     * Object containing channel details
     */
    private Channel channel;

    /**
     * 唯一请求 ID，用于标识一次 CheckUserRequest
     * Unique request Id, that identifies CheckUserRequest
     */
    private String uuid;

    /**
     * 内部类 - 渠道信息
     */
    @Data
    public static class Channel implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 玩家设备类型（M 表示移动设备，P 或其他表示非移动设备）
         * Player’s device type (M = mobile, P = anything else)
         */
        private String type;
    }
}
