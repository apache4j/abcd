package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import com.cloud.baowang.user.api.enums.UserAccountTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("user_transfer_agent")
@Schema(description = "UserTransferAgent对象")
public class UserTransferAgentPO extends BasePO implements Serializable {

    /**
     * 站点code
     */
    private String siteCode;

    /**
     * 转代会员账号
     */
    private String userAccount;
    /**
     * 会员id
     */
    private String userId;
    /**
     * {@link UserAccountTypeEnum}
     * 会员账号类型 1.测试，2.正式 ，关联system_param account_type枚举值
     */
    private Integer accountType;
    /**
     * 当前上级代理id
     */
    private String currentAgentId;
    /**
     * 当前上级代理账号
     */
    private String currentAgentName;
    /**
     * 转入上级代理id
     */
    private String transferAgentId;

    /**
     * 转入上级代理账号
     */
    private String transferAgentName;
    /**
     * 锁单人id
     */
    private String lockerId;
    /**
     * {@link com.cloud.baowang.common.core.enums.LockStatusEnum}
     * 锁单状态（0-未锁定 1-已锁定）
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
     * 审核状态（1-待处理 2-处理中，3-审核通过，4-审核拒绝）
     * 同system_param review_status code
     * {@link com.cloud.baowang.common.core.enums.ReviewStatusEnum}
     */
    private Integer auditStatus;
    /**
     * 审核完成时间
     */
    private Long auditDatetime;
    /**
     * 审核人id
     */
    private String auditId;
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
     * 申请人id
     */
    private String applyId;
    /**
     * 申请人
     */
    private String applyName;
    /**
     * 申请备注
     */
    private String applyRemark;
    /**
     * {@link com.cloud.baowang.common.core.enums.ReviewOperationEnum}
     * 审核环节 1.一审审核，2.结单查看 同system_param review_operation code
     */
    private Integer auditStep;

}
