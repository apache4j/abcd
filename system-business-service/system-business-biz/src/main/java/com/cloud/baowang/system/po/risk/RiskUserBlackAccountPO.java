package com.cloud.baowang.system.po.risk;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

@Data
@TableName("risk_user_black_account")
public class RiskUserBlackAccountPO extends BasePO {
    //账号
    private String riskControlAccount;
    //会员账号
    private String userAccount;
    //风控类型code
    private String riskControlTypeCode;
    //注册站点
    private String registerDomain;
    //注册时间
    private Long registerTime;
    //最后登录时间
    private Long lastLoginTime;
}
