package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import com.cloud.baowang.wallet.api.enums.balanceChange.BalanceChangeStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;

/**
 * 代理人工加减额记录
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Data
@Accessors(chain = true)
@FieldNameConstants
@TableName("agent_manual_up_down_record")
@Schema(description = "代理人工加减额记录")
public class AgentManualUpDownRecordPO extends BasePO {
    @Schema(description = "siteCode")
    private String siteCode;

    @Schema(description = "代理id")
    private String agentId;

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "代理姓名")
    private String agentName;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "钱包类型 1佣金钱包 2额度钱包")
    private Integer walletType;

    @Schema(description = "调整方式:1-加额，2-减额")
    private Integer adjustWay;

    /**
     * AgentManualAdjustTypeEnum
     */
    @Schema(description = "调整类型")
    private Integer adjustType;

    @Schema(description = "调整金额")
    private BigDecimal adjustAmount;

    @Schema(description = "上传附件地址")
    private String certificateAddress;

    @Schema(description = "申请原因")
    private String applyReason;

    @Schema(description = "申请时间")
    private Long applyTime;

    @Schema(description = "申请人")
    private String applicant;

    @Schema(description = "一审开始时间")
    private Long oneReviewStartTime;

    @Schema(description = "一审完成时间")
    private Long oneReviewFinishTime;

    @Schema(description = "一审人")
    private String oneReviewer;

    @Schema(description = "一审备注")
    private String oneReviewRemark;

    @Schema(description = "二审开始时间")
    private Long twoReviewStartTime;

    @Schema(description = "二审完成时间")
    private Long twoReviewFinishTime;

    @Schema(description = "二审人")
    private String twoReviewer;

    @Schema(description = "二审备注")
    private String twoReviewRemark;
    /**
     * {@link com.cloud.baowang.common.core.enums.AgentManualReviewOperationEnum}
     */
    @Schema(description = "审核操作")
    private Integer reviewOperation;

    /**
     * {@link  }
     */
    @Schema(description = "订单状态")
    private Integer orderStatus;
    /**
     * 0.账变失败，1.账变成功
     * {@link BalanceChangeStatusEnum}
     */
    private Integer balanceChangeStatus;

    @Schema(description = "锁单状态 0未锁 1已锁")
    private Integer lockStatus;

    @Schema(description = "锁单人")
    private String locker;

    @Schema(description = "代理提款（后台）是否大额出款;0-否，1-是")
    private String isBigMoney;

    @Schema(description = "最近操作人账号")
    private String lastOperator;


}
