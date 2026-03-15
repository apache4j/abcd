package com.cloud.baowang.agent.api.vo.merchant;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "商务登录信息分页查询vo")
public class AgentMerchantLoginInfoPageQueryVO extends PageVO implements Serializable {

    @Schema(description = "siteCode", hidden = true)
    private String siteCode;

    @Schema(description = "登录时间-开始")
    private Long createdTimeStart;

    @Schema(description = "登录时间-结束")
    private Long createdTimeEnd;


    @Schema(description = "登录状态")
    private Integer loginType;

    @Schema(description = "商务账号")
    private String merchantAccount;

    @Schema(description = "商务名称")
    private String merchantName;

    @Schema(description = "登录ip")
    private String loginIp;

    @Schema(description = "ip归属地")
    private String ipAddress;

    @Schema(description = "终端设备号")
    private String terminalDeviceNo;


}
