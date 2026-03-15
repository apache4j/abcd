package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 代理域名变更记录
 */
@Data
@TableName("agent_domain_record")
public class AgentDomainRecordPO extends BasePO implements Serializable {
    @Schema(description = "siteCode")
    private String siteCode;

    @Schema(description = "域名")
    private String domainName;

    @Schema(description = "变更类型")
    private Integer recordType;

    @Schema(description = "变更前")
    private String beforeText;

    @Schema(description = "变更后")
    private String afterText;

    @Schema(description = "备注")
    private String remark;


}
