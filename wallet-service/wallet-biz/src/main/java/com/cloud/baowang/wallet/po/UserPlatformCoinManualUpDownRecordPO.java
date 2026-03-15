package com.cloud.baowang.wallet.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import com.cloud.baowang.wallet.api.enums.balanceChange.BalanceChangeStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;

/**
 * 会员平台币上下分记录
 *
 * @author qiqi
 */
@Data
@Accessors(chain = true)
@FieldNameConstants
@TableName("user_platform_coin_manual_up_down_record")
@Schema(title = "会员平台币上下分记录")
public class UserPlatformCoinManualUpDownRecordPO extends BasePO {

    @Schema(title = "站点编码")
    private String siteCode;

    @Schema(title = "代理id")
    private String agentId;

    @Schema(title = "代理账号")
    private String agentAccount;

    @Schema(title = "会员账号")
    private String userAccount;

    @Schema(title = "会员ID")
    private String userId;

    @Schema(title = "姓名")
    private String userName;

    @Schema(title = "vip等级code")
    private Integer vipGradeCode;

    @Schema(title = "订单号")
    private String orderNo;
    /**
     * {@link com.cloud.baowang.common.core.enums.manualDowmUp.PlatformCoinManualAdjustWayEnum}
     * 1.平台币上分，2.平台币下分
     */
    @Schema(title = "调整方式")
    private Integer adjustWay;

    /**
     * {@link com.cloud.baowang.common.core.enums.manualDowmUp.PlatformCoinManualDownAdjustTypeEnum}
     * {@link com.cloud.baowang.common.core.enums.manualDowmUp.PlatformCoinManualUpAdjustTypeEnum}
     */
    @Schema(title = "调整类型")
    private Integer adjustType;
    /**
     * 活动模板system_param activity_template code值
     */
    @Schema(title = "活动类型")
    private String activityTemplate;
    /**
     * 活动ID-短id
     */
    @Schema(title = "活动ID")
    private String activityId;
    /**
     * 币种code
     */
    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "平台币币种")
    private String platformCurrencyCode;

    @Schema(title = "调整金额")
    private BigDecimal adjustAmount;

    @Schema(title = "流水倍数")
    private BigDecimal runningWaterMultiple;

    @Schema(title = "上传附件地址")
    private String certificateAddress;

    @Schema(title = "申请原因")
    private String applyReason;

    @Schema(title = "申请时间")
    private Long applyTime;

    @Schema(title = "申请人")
    private String applicant;

    @Schema(title = "审核时间")
    private Long auditDatetime;


    @Schema(title = "审核人")
    private String auditId;

    @Schema(title = "审核备注")
    private String auditRemark;
    /**
     * {@link com.cloud.baowang.common.core.enums.manualDowmUp.PlatformCoinReviewStatusEnum}
     */
    @Schema(title = "审核状态（1-待处理 2-处理中，3-审核通过，4-审核拒绝）")
    private Integer auditStatus;
    /**
     * 审核操作
     * {@link com.cloud.baowang.common.core.enums.ReviewOperationEnum}
     */
    @Schema(title = "1.一审审核，2.结单查看")
    private Integer reviewOperation;
    /**
     * 账变状态，0.失败，1.成功
     * {@link BalanceChangeStatusEnum}
     */
    private Integer balanceChangeStatus;


    @Schema(title = "锁单状态 0未锁 1已锁")
    private Integer lockStatus;

    @Schema(title = "锁单人")
    private String locker;


}
