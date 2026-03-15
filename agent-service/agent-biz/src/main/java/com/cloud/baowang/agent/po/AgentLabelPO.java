/**
 * @(#)AgentLabel.java, 10月 12, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

@Data
@TableName("agent_label")
public class AgentLabelPO extends BasePO {
    /**
     * 所属站点
     */
    private String siteCode;
    /**
     * 标签名
     */
    private String name;
    /**
     * 标签描述
     */
    private String description;
    /**
     * 操作人
     */
    private String operator;

    /**
     * {@link com.cloud.baowang.common.core.enums.EnableStatusEnum}
     * 删除状态
     */
    private Integer deleted;


}
