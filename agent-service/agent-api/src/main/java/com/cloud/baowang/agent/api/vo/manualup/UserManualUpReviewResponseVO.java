package com.cloud.baowang.agent.api.vo.manualup;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: kimi
 */
@Data
@Schema(title = "会员人工加额审核-列表 返回")
@I18nClass
public class UserManualUpReviewResponseVO {

    @Schema(title = "id")
    private String id;

    @Schema(title = "操作")
    private String operation;

    @Schema(title = "订单号")
    private String orderNo;

    @Schema(description = "会员ID")
    private String userId;

    @Schema(title = "会员注册信息")
    private String userAccount;

    @Schema(title = "会员对应代理id")
    private String agentId;

    @Schema(title = "调整类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_MANUAL_UP_ADJUST_TYPE)
    private Integer adjustType;
    @Schema(title = "调整类型 - Text")
    private String adjustTypeText;

    @Schema(title = "调整金额")
    private BigDecimal adjustAmount;

    @Schema(title = "申请时间")
    private Long applyTime;

    @Schema(title = "审核状态")
    private Integer orderStatus;
    @Schema(title = "审核状态 - Text")
    private String orderStatusText;

    @Schema(title = "锁单状态 0未锁 1已锁")
    private Integer lockStatus;

    @Schema(title = "审核员/锁单人")
    private String locker;

    @Schema(title = "锁单人是否当前登录人 0否 1是")
    private Integer isLocker;

    @Schema(title = "申请人")
    private String applicant;
    @Schema(title = "申请人是否当前登录人 0否 1是")
    private Integer isApplicant;

    @Schema(title = "一审人")
    private String oneReviewer;
    @Schema(title = "一审人是否当前登录人 0否 1是")
    private Integer isOneReviewer;
}
