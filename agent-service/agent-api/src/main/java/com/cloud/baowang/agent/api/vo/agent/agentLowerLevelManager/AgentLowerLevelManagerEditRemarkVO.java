package com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/6/17 15:20
 * @Version: V1.0
 **/
@Data
@Schema(description = "")
public class AgentLowerLevelManagerEditRemarkVO {
    @Schema(description ="下级代理账号")
    @NotBlank(message = "下级代理账号不能为空")
    private String agentAccount;
    @Schema(description ="站点编号",hidden = true)
    private String siteCode;

    @Length( max = 400)
    @Schema(description ="备注")
    private String remark;
}
