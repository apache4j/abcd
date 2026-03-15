package com.cloud.baowang.agent.api.vo.user;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: kimi
 */
@Data
@Schema(title ="代理H5 下级概览")
public class AgentOverviewResponseVO {

    @Schema(title  ="下级用户")
    private Integer lowerLevelUser;

    @Schema(title  ="本期新注册")
    private Integer newRegisterCount;

    @Schema(title  ="本期有效新增")
    private Integer validNewUserCount;

    @Schema(title  ="本期有效活跃")
    private Integer validActiveUsers;
}
