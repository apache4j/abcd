package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;

import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 代理标签配置实体
 */
@Data
@TableName("agent_label_record")
public class AgentLabelRecordPO extends BasePO implements Serializable {
    /**
     * 所属站点
     */
    private String siteCode;
    /**
     * {@link com.cloud.baowang.agent.api.enums.AgentLabelChangeEnum}
     * 0.修改名称,1.修改描述,2新增,3.删除，同system_param agent_label_operation_type code值
     */
    private Integer type;
    /**
     * 标签id
     */
    private String agentLabelId;
    /**
     * 标签名称
     */
    private String agentLabelName;
    /**
     * 变更前
     */
    private String changeBefore;
    /**
     * 变更后
     */
    private String changeAfter;
    /**
     * 操作人
     */
    private String operator;
}
