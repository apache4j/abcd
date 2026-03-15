/**
 * @(#)AgentLabelUserVO.java, 10月 13, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.agent.api.vo.label;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <h2></h2>
 * @author wayne
 * date 2023/10/13
 */
@Data
@Schema( title = "代理标签人员对象", description = "代理标签人员对象")
@I18nClass
public class AgentLabelUserVO {
    @Schema( title = "代理账号")
    private String agentAccount;

    /**
     * {@link com.cloud.baowang.agent.api.enums.AgentTypeEnum}
     */
    @Schema( title = "代理类型 1正式 2测试 3合作")
    private Integer agentType;

    @Schema( title = "代理类型名称")
    @I18nField
    private String agentTypeName;
}
