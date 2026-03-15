package com.cloud.baowang.system.po.risk;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

@Data
@TableName("risk_ctrl_black_account")
public class RiskCtrlBlackAccountPO extends BasePO {
    // 账号
    private String riskControlAccount;
    // 风控类型code
    /**{@link com.cloud.baowang.system.api.enums.RiskBlackTypeEnum}*/
    private String riskControlTypeCode;
    // 风控账号数量
    private Integer riskCount;
    // IP白名单
    private String ipWhitelist;
    // 备注
    private String remark;
    // 账号名称
    private String riskControlAccountName;
}
