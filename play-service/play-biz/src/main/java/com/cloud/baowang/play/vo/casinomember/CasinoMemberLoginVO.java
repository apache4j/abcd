package com.cloud.baowang.play.vo.casinomember;

import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class CasinoMemberLoginVO extends BasePO implements Serializable {
    /**
     * 账户名
     */
    private String userAccount;
    /**
     * 娱乐城用户名
     */
    private String venueUserAccount;
    /**
     * 三方平台
     */
    private String venuePlatform;
    /**
     * 第三方平台code
     */
    private String venueCode;
    /**
     * 最后登入时间
     */
    private Long lastLoginTime;

    private String userId;

    /**
     * 站点编码
     */
    private String siteCode;
}
