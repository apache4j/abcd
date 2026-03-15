package com.cloud.baowang.agent.api.vo.agentreview.info;


import com.cloud.baowang.common.core.vo.base.SitePageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author : 小智
 * @Date : 12/10/23 4:12 PM
 * @Version : 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "代理基本信息返回对象")
public class AgentDetailParam extends SitePageVO implements Serializable {

    @Schema(description ="代理账号")
    @NotNull(message = "代理账号不能为空")
    private String agentAccount;

    private Boolean dataDesensitization;

    /**
     * 时区
     */
    private String timeZone;
}
