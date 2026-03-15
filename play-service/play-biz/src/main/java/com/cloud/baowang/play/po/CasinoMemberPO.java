package com.cloud.baowang.play.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@TableName(value = "casino_member")
public class CasinoMemberPO extends BasePO implements Serializable {
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
     * 账号状态: (0失败 1 成功)
     */
    private Integer status;
    /**
     * 站点编码
     */
    private String siteCode;
}
