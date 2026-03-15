package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.agent.api.enums.AgentInfoChangeTypeEnum;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("agent_info_modify_review")
@Schema(description = "代理信息变更申请表")
public class AgentInfoModifyReviewPO extends BasePO {
    /**
     * siteCode
     */
    private String siteCode;
    /**
     * 申请时间 提交审核的时间信息
     */
    private Long applicationTime;
    /**
     * 一审完成时间 一审完成后的的时间信息
     */
    private Long firstReviewTime;
    /**
     * 审核单号 系统生成
     */
    private String reviewOrderNumber;
    /**
     * {@link com.cloud.baowang.common.core.enums.ReviewOperationEnum}
     * 审核操作 1.一审审核，2.结单查看
     */
    private Integer reviewOperation;
    /**
     * 审核状态
     * {@link com.cloud.baowang.common.core.enums.ReviewStatusEnum}
     */
    private Integer reviewStatus;
    /**
     * 申请人
     */
    private String applicant;
    /**
     * 一审人
     */
    private String firstInstance;

    /**
     * 锁单状态 1锁单 0 未锁
     * {@link com.cloud.baowang.common.core.enums.LockStatusEnum}
     */
    private Integer lockStatus;

    /**
     * 审核申请类型
     * {@link  AgentInfoChangeTypeEnum}
     */
    private Integer reviewApplicationType;

    /**
     * 代理账号
     */
    private String agentAccount;
    /**
     * {@link com.cloud.baowang.agent.api.enums.AgentTypeEnum}
     * 账号类型
     */
    private Integer agentType;
    /**
     * 修改前信息
     */
    private String beforeFixing;
    /**
     * 修改后信息
     */
    private String afterModification;
    /**
     * 锁单人
     */
    private String locker;
    /**
     * 审核备注
     */
    private String reviewRemark;
    /**
     * 申请信息
     */
    private String applicationInformation;
    //修改后json信息
    private String afterModificationJson;
}
