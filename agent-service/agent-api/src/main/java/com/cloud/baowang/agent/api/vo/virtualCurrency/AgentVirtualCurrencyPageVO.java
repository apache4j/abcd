package com.cloud.baowang.agent.api.vo.virtualCurrency;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * @author qiqi
 */
@Data
@Schema(description = "代理虚拟币地址分页查询对象")
public class AgentVirtualCurrencyPageVO extends PageVO {


    @Schema(description = "使用开始时间")
    private Long useStartTime;

    @Schema(description = "使用结束时间")
    private Long useEndTime;

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "代理类型")
    private String agentType;


    @Schema(description = "风控层级ID")
    private String riskControlLevelId;


    @Schema(description = "虚拟币账号地址")
    private String virtualCurrencyAddress;

    @Schema(description = "虚拟币种类")
    private String virtualCurrencyType;

    @Schema(description = "虚拟币协议")
    private String virtualCurrencyProtocol;



}
