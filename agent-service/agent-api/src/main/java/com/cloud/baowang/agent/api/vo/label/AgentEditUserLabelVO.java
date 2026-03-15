/**
 * @(#)AgentLabel.java, 10月 12, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.agent.api.vo.label;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * <h2></h2>
 * @author wayne
 * date 2023/10/12
 */
@Data
@Schema( title = "代理编辑会员标签", description = "代理标签管理-代理编辑会员标签")
public class AgentEditUserLabelVO {

    @NotBlank(message = "排序不能为空")
    @Schema( title = "修改之前标签名称")
    private String label;
    
    @Schema( title = "代理账号", hidden = true)
    private String agentAccount;


}
