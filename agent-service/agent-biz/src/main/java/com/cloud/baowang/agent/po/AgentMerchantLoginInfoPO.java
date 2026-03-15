package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

import java.io.Serializable;

@TableName("agent_merchant_login_info")
@Data
public class AgentMerchantLoginInfoPO extends BasePO implements Serializable {


    /**
     * 登录状态
     */
    private Integer loginType;

    /**
     * 商务账号
     */
    private String merchantAccount;

    /**
     * 商务名称
     */
    private String merchantName;

    /**
     * 登录ip
     */
    private String loginIp;
    /**
     * ip归属地
     */
    private String ipAddress;

    /**
     * 登录网址
     */
    private String loginAddress;

    /**
     * 登录终端
     */
    private String loginTerminal;

    /**
     * 终端设备号
     */
    private String terminalDeviceNo;

    /**
     * 备注
     */
    private String remark;

    /**
     * 站点
     */
    private String siteCode;


}
