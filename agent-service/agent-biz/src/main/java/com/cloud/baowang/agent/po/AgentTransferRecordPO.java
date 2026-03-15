package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author : 小智
 * @Date : 19/10/23 9:06 PM
 * @Version : 1.0
 */
@Data
@TableName("agent_transfer_record")
public class AgentTransferRecordPO extends BasePO implements Serializable {

    /**
     * 站点编码
     */
    private String siteCode;

    /* 代理编号 */
    private String agentId;

    /* 订单号 */
    private String orderNo;

    /* 代理账号 */
    private String agentAccount;

    /* 转账时间 */
    private Long transferTime;

    /* 佣金日期，只针对佣金转账有效*/
    private Long reportDay;

    /* 转账类型 */
    private String transferType;

    /* 转账金额 */
    private BigDecimal transferAmount;
    /* 转账账号id */
    private String transferAgentId;
    /* 转账账号 */
    private String transferAccount;

    /* 备注 */
    private String remark;

    /* 转账状态 */
    private Integer transferStatus;

}
