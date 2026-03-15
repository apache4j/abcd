package com.cloud.baowang.system.api.vo.param;


import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "代理参数配置VO")
public class AgentParamConfigVO extends PageVO implements Serializable {

    @Schema(description = "Id")
    private String id;

    @Schema(description = "类型: 1=固定值、2=充值金额,3=有效投注")
    private Integer paramType;

    @Schema(description = "值")
    private String paramValue;


    @Schema(description = "更新人", hidden = true)
    private String updater;

    @Schema(description = "登陆者", hidden = true)
    private String agentAccount;




}
