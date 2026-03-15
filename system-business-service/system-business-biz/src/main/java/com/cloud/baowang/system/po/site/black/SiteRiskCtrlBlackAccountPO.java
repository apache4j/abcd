package com.cloud.baowang.system.po.site.black;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

@Data
@TableName("site_risk_ctrl_black_account")
public class SiteRiskCtrlBlackAccountPO extends BasePO {
    private String siteCode;
    // 账号
    private String riskControlAccount;
    // 风控类型code
    private String riskControlTypeCode;
    // 风控账号数量
    private Integer riskCount;
    // IP白名单
    private String ipWhitelist;
    // 备注
    private String remark;
    private String riskControlAccountName;
}
