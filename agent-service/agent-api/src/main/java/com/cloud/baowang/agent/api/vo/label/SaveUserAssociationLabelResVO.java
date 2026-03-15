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
 *
 * @author wayne
 * date 2023/10/12
 */
@Data
@Schema(title = "代理保存会员保存用户关联标签请求对象", description = "代理保存会员保存用户关联标签请求对象")
public class SaveUserAssociationLabelResVO {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;

    @NotBlank(message = "标签名称不能为空")
    @Schema(title = "需要保存的标签名称集合，用,隔开")
    private String labels;

    @NotBlank(message = "会员账号不能为空")
    @Schema(title = "会员账号集合，用,隔开")
    private String userAccounts;

    @Schema(title = "代理账号", hidden = true)
    private String agentAccount;

    @Schema(title = "代理账号ID", hidden = true)
    private String agentAccountId;


}
