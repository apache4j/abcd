package com.cloud.baowang.agent.api.vo.agentLogin;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author : kimi
 * @Date : 11/10/23 5:52 PM
 * @Version : 1.0
 */
@Data
@Schema(title = "代理登录记录 传入参数")
public class AgentLoginRecordParam extends PageVO implements Serializable {

    @Schema(title = "站点code",hidden = true)
    private String siteCode;

    //"站点时区")
    private String timezone;

    @Schema(title = "代理账号")
    private String agentAccount;

    @Schema(title = "代理类型")
    private String agentType;

    @Schema(title = "登录状态")
    private String loginStatus;

    @Schema(title = "登录IP")
    private String loginIp;

    @Schema(title = "IP归属地")
    private String ipAttribution;

    @Schema(title = "登录终端")
    private List<String> loginDevice;

    @Schema(title = "终端设备号")
    private String deviceNumber;

    @Schema(title = "开始时间")
    private Long startTime;

    @Schema(title = "结束时间")
    private Long endTime;


}
