package com.cloud.baowang.agent.api.vo.agentImage;

import com.cloud.baowang.common.core.vo.base.PageVO;
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
public class AgentImageRecordVO extends PageVO implements Serializable {

    @Schema(description = "siteCode")
    private String siteCode;

    @Schema(description = "变更类型")
    private Integer recordType;

    @Schema(description = "修改者的账号")
    private String updateName;

    @Schema(description = "变更时间起点")
    private Long updateTimeBegin;

    @Schema(description = "变更时间终点")
    private Long updateTimeEnd;


}
