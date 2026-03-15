package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@TableName("agent_domain")
public class AgentDomainPO extends BasePO implements Serializable {
    /**
     * 所属站点
     */
    private String siteCode;

    /**
     * 域名
     */
    private String domainName;

    /**
     * 域名描述
     */
    private String domainDescription;

    /**
     * system_param site_domain_type code值
     */
    private Integer domainType;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 域名备注
     */
    private String remark;


}
