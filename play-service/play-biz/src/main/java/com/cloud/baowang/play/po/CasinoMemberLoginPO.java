package com.cloud.baowang.play.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@TableName(value = "casino_member_login")
public class CasinoMemberLoginPO extends BasePO implements Serializable {
    /**
     * 账户名
     */
    private String userAccount;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 娱乐城用户名
     */
    private String venueUserAccount;
    /**
     * 三方平台
     */
    private String venuePlatform;
    /**
     * 第三方平台
     */
    private String venueCode;
    /**
     * 最后登入时间
     */
    private Long lastLoginTime;
    /**
     * 站点编码
     */
    private String siteCode;
}
