package com.cloud.baowang.user.api.vo.user;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author 小智
 * @Date 15/5/23 4:11 PM
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "会员登录日志传入参数")
public class UserLoginRequestVO extends PageVO implements Serializable {

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "账号类型")
    private List<Integer> accountType;

    @Schema(description = "登录状态")
    private Integer loginType;

    @Schema(description = "登录IP")
    private String ip;

    @Schema(description = "IP归属地")
    private String ipAddress;

    @Schema(description = "登录终端")
    private List<Integer> loginTerminal;

    @Schema(description = "终端设备号")
    private String deviceNo;

    @Schema(description = "登录开始时间")
    private Long loginStartTime;

    @Schema(description = "登录结束时间")
    private Long loginEndTime;

    @Schema(title = "站点code",hidden = true)
    private String siteCode;

    private Boolean dataDesensitization = true;

    @Schema(title = "代理账号")
    private String superAgentAccount;

    @Schema(title = "去重查询模式 true  false")
    private Boolean distinct = false;

}
