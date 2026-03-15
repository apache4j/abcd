package com.cloud.baowang.system.po.risk;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("risk_change_record")
public class RiskChangeRecordPO extends BasePO {
    //账号
    private String riskControlAccount;
    //风控类型
    private String riskControlType;
    //风控前层级
    private String riskBefore;
    //风控后层级
    private String riskAfter;
    //风控原因
    private String riskDesc;
    /**
     * 站点code
     */
    private String siteCode;

}
