package com.cloud.baowang.play.api.vo.order;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class CasinoMemberVO implements Serializable {
    private String id;
    /**
     * 账户名
     */
    private String userAccount;
    /**
     * 游戏账号
     */
    private String venueUserAccount;
    /**
     * 三方平台
     */
    private String venuePlatform;
    /**
     * 场馆名称
     */
    private String venueCode;
    /**
     * 本地用户id
     */
    private String userId;
    /**
     * 三方游戏用户id
     */
    private String venueUserId;
    /**
     * 场馆密码
     */
    private String casinoPassword;

    private String ip;
    /**
     * 账号状态: (0关闭, 1:启用, -1:三方创建不成功)
     */
    private Integer status;

    private String creator;

    private Long createdTime;

    private String updater;

    private Long updatedTime;
    /**
     * 站点编码
     */
    private String siteCode;

    private String currencyCode;
}
