/**
 * @(#)AgentLabel.java, 10月 12, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("agent_security_question_config")
@Schema(description = "代理密保问题对象")
public class AgentSecurityQuestionConfigPO extends BasePO implements Serializable {

    @Schema(description = "密保问题")
    private String securityQuestion;

    @Schema(description = "状态 1启用 0停用")
    private Integer status;

    @Schema(description = "操作人")
    private String operator;

    @Schema(description = "创建人")
    private String creatorName;

}
