package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import com.cloud.baowang.user.api.enums.UserAccountTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 会员溢出审核表
 *
 * @author aomiao
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("agent_user_overflow")
public class AgentUserOverflowPO extends SiteBasePO implements Serializable {
    /**
     * 会员账号ID
     */
    private String memberName;

    /**
     * 会员id
     */
    private String userId;
    /**
     * 溢出会员注册信息
     */
    private String userRegister;

    /**
     * {@link UserAccountTypeEnum}
     * 会员账号类型,1-测试,2-正式 同system_param account_type code值
     */
    private Integer accountType;
    /**
     * 转入上级代理账号
     */
    private String transferAgentName;
    /**
     * 转入上级代理id
     */
    private String transferAgentId;
    /**
     * 代理类型 1正式 2测试 3合作 同system_param agent_type code 值
     * {@link com.cloud.baowang.agent.api.enums.AgentTypeEnum}
     */
    private Integer agentType;
    /**
     * 推广设备 1APP 2PC 3H5 (没有找到在system_param中的定义)
     * {@link com.cloud.baowang.agent.api.enums.AgentOverFlowDeviceTypeEnum}
     */
    private Integer device;
    /**
     * 推广链接
     */
    private String link;
    /**
     * 上传图片
     */
    private String image;

    /**
     * 锁单状态（0-未锁定 1-已锁定）同system_param中lock_status code值
     * {@link com.cloud.baowang.common.core.enums.LockStatusEnum}
     */
    private Integer lockStatus;
    /**
     * 锁单时间
     */
    private Long lockDatetime;
    /**
     * 锁单人
     */
    private String lockName;
    /**
     * 审核状态（1-待处理 2-处理中，3-审核通过，4-审核拒绝）同system_param review_status code
     * {@link com.cloud.baowang.common.core.enums.ReviewStatusEnum}
     */
    private Integer auditStatus;
    /**
     * 审核完成时间
     */
    private Long auditDatetime;
    /**
     * 审核人
     */
    private String auditName;
    /**
     * 审核备注
     */
    private String auditRemark;
    /**
     * 审核单号
     */
    private String eventId;
    /**
     * 申请人
     */
    private String applyName;
    /**
     * 申请备注
     */
    private String applyRemark;
    /**
     * 审核环节 1.一审审核，2.结单查看 同system_param review_operation code
     * {@link com.cloud.baowang.common.core.enums.ReviewOperationEnum}
     */
    private Integer auditStep;
    /**
     * {@link com.cloud.baowang.agent.api.enums.UserOverFlowSourceEnums}
     * 当前申请来源 0.代理端发起,1.站点后台发起
     */
    private Integer applySource;
}
