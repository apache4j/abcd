package com.cloud.baowang.system.po.operations;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("domain_info")
public class DomainInfoPO extends BasePO {

    private String domainAddr;

    private String siteCode;

    private Integer domainType;
    /**
     * 绑定状态
     */
    private Integer bind;

    private String remark;

}
