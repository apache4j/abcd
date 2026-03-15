package com.cloud.baowang.play.vo.casinomember;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CasinoMemberReq implements Serializable {

    private String id;
    /**
     * 账户名
     */
    private String userAccount;

    private List<String> userAccountList;
    /**
     * 娱乐城用户名
     */
    private String venueUserAccount;

    private List<String> venueUserAccountList;
    /**
     * 三方平台
     */
    private String venuePlatform;
    /**
     * 密码
     */
    private String casinoPassword;
    /**
     * 三方游戏平台code
     */
    private String venueCode;
    /**
     * 账号状态: (0关闭, 1:启用, -1:三方创建不成功)
     */
    private Integer status;

    private Long loginTime;

    private String venueUserId;

    private String siteCode;

    private String userId;
}
