/**
 * @(#)AgentLabel.java, 10月 12, 2023.
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
 *
 * @author wayne
 * date 2023/10/12
 */
@Data
@Schema(title = "代理标签配置变更记录返回对象", description = "代理标签配置变更记录返回对象")
@I18nClass
public class AgentLabelRecordListVO {
    @Schema(title = "主键")
    private String id;
    /**
     * {@link com.cloud.baowang.agent.api.enums.AgentLabelChangeEnum}
     */
    @Schema(title = "操作类型 0.修改名称,1.修改描述,2新增,3.删除，同system_param agent_label_operation_type code值")
    private Integer type;
    @I18nField
    @Schema(title = "类型名称")
    private String typeName;
    @Schema(title = "创建时间")
    private Long createdTime;
    @Schema(title = "操作时间")
    private Long updatedTime;
    @Schema(title = "标签名称")
    private String agentLabelName;
    @Schema(title = "变更前")
    private String changeBefore;
    @Schema(title = "变更后")
    private String changeAfter;
    @Schema(title = "操作人")
    private String operator;


}
