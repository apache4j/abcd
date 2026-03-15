package com.cloud.baowang.system.po.risk;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

@Data
@TableName("risk_account")
public class RiskAccountPO extends BasePO {
    //账号
    private String riskControlAccount;
    //风控类型
    private String riskControlType;
    //风控类型code
    private String riskControlTypeCode;
    //风控层级
    private String riskControlLevel;
    //风控层级ID
    private String riskControlLevelId;
    //风控原因
    private String riskDesc;
    //电子钱包风控-对应电子钱包提款方式id
    private String withdrawWayId;

    /**
     * 站点code
     */
    @TableField(value = "site_code")
    private String siteCode;
}
