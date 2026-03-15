package com.cloud.baowang.agent.api.vo.remark;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "代理备注 返回信息")
public class AgentRemarkRecordVO {


    @Schema(description = "站点编号")
    private String siteCode;

    @Schema(description = "备注内容")
    private String remark;

    @Schema(description = "备注账号")
    private String operator;

    @Schema(description = "更新时间")
    private Long updateTime;
}
