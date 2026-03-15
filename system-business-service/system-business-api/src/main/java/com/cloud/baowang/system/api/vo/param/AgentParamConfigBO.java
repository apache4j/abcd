package com.cloud.baowang.system.api.vo.param;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "代理参数配置BO")
public class AgentParamConfigBO  implements Serializable {
    private String id;

    @Schema(description ="名称代码")
    private String paramCode;

    @Schema(description ="名称")
    private String paramName;

    @Schema(description ="类型: 1=固定值、2=充值金额,3=有效投注")
    private Integer paramType;

    @Schema(description ="值")
    private String paramValue;

    @Schema(description ="值")
    private String paramValueName;

    @Schema(description ="类型限制: 1=固定值、2=充值金额,3=有效投注")
    private Integer paramTypeLimit;

    @Schema(description ="创建者的账号")
    private String createName;

    @Schema(description ="修改者的账号")
    private String updateName;

    @Schema(description ="登陆者", hidden = true)
    private String agentAccount;

    @Schema(description ="创建人")
    private String creator;

    @Schema(description ="创建时间")
    private Long createdTime;

    @Schema(description ="更新人")
    private Long updater;
    @Schema(description ="更新时间")
    private Long updatedTime;

}
