package com.cloud.baowang.play.api.vo.casinoMember;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CasinoMemberRespVO {

    /**
     * 账户名
     */
    private String userAccount;
    /**
     * 游戏账号
     */
    private String venueUserAccount;
    /**
     * 登录密码
     */
    private String casinoPassword;
    /**
     * 本地用户id
     */
    private String userId;
    /**
     * 三方游戏用户id
     */
    private String venueUserId;

    /**
     * 三方平台
     */
    private String venuePlatform;
    /**
     * 场馆名称
     */
    private String venueCode;
    /**
     * 场馆名称
     */
    private String siteCode;




}
