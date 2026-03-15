package com.cloud.baowang.wallet.api.vo.userCoinManualDown;

import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Schema(description = "会员人工扣除记录")
@I18nClass
public class UserManualDownRecordVO implements Serializable {

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "站点编码")
    private String siteCode;

    @Schema(description = "代理id")
    private String agentId;

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "会员注册信息(手机号码或邮箱)")
    private String userRegister;

    @Schema(description = "姓名")
    private String userName;

    @Schema(description = "vip等级")
    private Integer vipGradeCode;
    @Schema(description = "vip等级名称")
    private String vipGradeCodeName;

    @Schema(description = "调整方式")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.MANUAL_ADJUST_WAY)
    private Integer adjustWay;
    @Schema(description = "调整方式 - Text")
    private String adjustWayText;

    @Schema(description = "订单状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_REVIEW_STATUS)
    private Integer reviewStatus;

    @Schema(title = "订单状态 - Text")
    private String reviewStatusText;

    @Schema(description = "调整类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.MANUAL_ADJUST_DOWN_TYPE)
    private Integer adjustType;
    @Schema(title = "调整类型 - Text")
    private String adjustTypeText;

    @Schema(description = "账变状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.BALANCE_CHANGE_STATUS)
    private Integer balanceChangeStatus;
    @Schema(description = "账变状态")
    private String balanceChangeStatusText;

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "调整金额")
    private BigDecimal adjustAmount;

    public BigDecimal getAdjustAmount() {
        if (ObjectUtil.isEmpty(adjustAmount)) {
            adjustAmount = BigDecimal.valueOf(0.0);
        }
        return adjustAmount;
    }

    @Schema(description = "操作时间")
    private String time;

    public String getTime() {
        if (null != createdTime) {
            return DateUtils.convertDateToString(new Date(createdTime));
        }
        return null;
    }

    @Schema(description = "上传附件地址")
    private String certificateAddress;
    @Schema(description = "上传附件地址URL")
    private String certificateAddressUrl;
    @Schema(description = "备注")
    private String applyReason;

    @Schema(description = "操作人")
    private String creator;
    @Schema(description = "操作时间")
    private Long createdTime;
    @Schema(description = "最后修改时间")
    private Long updatedTime;

    @Schema(title = "会员ID")
    private String userId;

    private Integer depositWithDrawType;

    private Integer adjustTimes;

}
