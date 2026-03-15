package com.cloud.baowang.agent.api.vo.agentLogin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author: fangfei
 * @createTime: 2024/06/17 21:36
 * @description: 代理登录返回对象VO
 */
@Data
@Schema(title = "AgentLoginResultVO", description = "代理登录返回信息")
public class AgentLoginResultVO {

    @Schema(title = "代理id")
    private String id;

    @Schema(title = "代理层级")
    private Integer level;

    @Schema(title = "代理账号")
    private String agentAccount;

    @Schema(title = "代理类型")
    private Integer agentType;

    @Schema(title = "代理类别")
    private Integer agentCategory;

    @Schema(title = "账号状态 1正常 2登录锁定 3充提锁定(状态多选,用逗号分开)")
    private String status;

    @Schema(title = "token")
    private String token;

    @Schema(title = "是否最底层代理 1是 0否")
    private Integer lowestLevelAgent;

    @Schema(title = "是否有支付密码(0:有支付密码,1:无支付密码)")
    private Integer isPayPassword;

    @Schema(title = "是否设置密保问题 1设置 0未设置")
    private Integer securitySet;

    @Schema(title = "是否是新IP登录  0 不是  1 是")
    private Integer isNewIp;

    @Schema(description = "谷歌验证秘钥")
    private String googleAuthKey;

    @Schema(description = "手机号码")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "佣金方案")
    private String planCode;

    @Schema(description = "会员福利 多个中间逗号分隔")
    private String userBenefit;

    @Schema(description = "当前平台币种名称")
    private String platCurrencyName;

    @Schema(description = "当前平台币种符号")
    private String platCurrencySymbol;

    @Schema(description = "当前平台时区")
    private String timeZone;
}
