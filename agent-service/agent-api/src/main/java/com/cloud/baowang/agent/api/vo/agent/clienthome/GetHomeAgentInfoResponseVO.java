package com.cloud.baowang.agent.api.vo.agent.clienthome;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: kimi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "个人资料 代理信息 ResponseVO")
public class GetHomeAgentInfoResponseVO {

    @Schema(description = "姓名")
    private String agentName;

    @Schema(description = "下级会员")
    private Long lowerLevelUserNumber;

    @Schema(description = "合营代码")
    private String inviteCode;
}
