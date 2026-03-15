package com.cloud.baowang.user.api.vo.user;

import com.cloud.baowang.user.api.vo.user.reponse.UserIpVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "会员登录返回信息对象")
public class UserLoginRspVO implements Serializable {

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "会员ID")
    private String userId;

    @Schema(description = "账号类型 1测试 2正式")
    private Integer accountType;

    @Schema(description = "账号状态 1正常 2登录锁定 3游戏锁定 4充提锁定")
    private String accountStatus;

    @Schema(description = "用户当前登录IP信息")
    private UserIpVO userIpVO;

    @Schema(description = "token")
    private String token;

    @Schema(description = "主货币")
    private String mainCurrency;

    @Schema(description = "登录状态 是否第一次登录")
    private Boolean firstLogin;

    @Schema(description = "新手指引步骤")
    private Integer step;
    @Schema(description = "任务领取状态 0-可领取 1-已领取 2-奖励已过期")
    private Integer receiveStatus;

    @Schema(description = "fb相关信息")
    private AgentOtherVO agentOtherVO;

}
