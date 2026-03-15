package com.cloud.baowang.activity.api.vo.finance;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/10/26 10:02
 * @Version: V1.0
 **/
@Data
@Schema(description = "会员活动记录返回参数")
@I18nClass
@ExcelIgnoreUnannotated
public class ActivityFinanceRespVO {
    /**
     * 站点code
     */
    @Schema(description = "站点编号")
    private String siteCode;
    /**
     * 订单号
     */
    @Schema(description = "订单号")
    @ExcelProperty("订单号")
    private String orderNo;
    /**
     * 所属活动
     */
    @Schema(description = "活动编号")
    @ExcelProperty("活动ID")
    private String activityNo;

    /**
     * 所属活动名称
     */
    @Schema(description = "活动名称")
    @ExcelProperty("活动名称")
    @I18nField
    private String activityNameI18nCode;

    /**
     * 活动模板 ACTIVITY_TEMPLATE
     * {@link com.cloud.baowang.activity.api.enums.ActivityTemplateEnum}
     */
    @Schema(description = "活动模板")
//    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.ACTIVITY_TEMPLATE)
    private String activityTemplate;

    @Schema(description = "活动模板名称")
    @ExcelProperty("活动模板")
    private String activityTemplateText;
    /**
     * 会员id
     */
    private String userId;
    /**
     * 会员账号
     */
    @Schema(description = "会员账号")
    @ExcelProperty("会员账号")
    private String userAccount;


    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.USER_ACCOUNT_TYPE)
    private String accountType;
    @Schema(description = "账号类型")
    @ExcelProperty("账号类型")
    private String accountTypeText;


    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.ACTIVITY_DISTRIBUTION_TYPE)
    private String receiveWay;

    @Schema(description = "领取方式")
    @ExcelProperty("领取方式")
    private String receiveWayText;


    /**{ ActivityReceiveStatusEnum}
     * 领取状态
     */
    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.ACTIVITY_RECEIVE_STATUS)
    private Integer receiveStatus;

    @Schema(description = "领取状态")
    @ExcelProperty("领取状态")
    private String receiveStatusText;


    private String activityRewardType;
    @Schema(description = "福利类型")
    @ExcelProperty("福利类型")
    private String activityRewardTypeText;

    @Schema(description = "旋转次数")
    @ExcelProperty("旋转次数")
    private Long wheelNum;


    /**
     * 币种
     */
    @Schema(description = "发放币种")
    @ExcelProperty("发放币种")
    private String currencyCode;

    /**
     * 发放礼金时的汇率
     */
    @Schema(description = "汇率")
    private BigDecimal finalRate;

    /**
     * 活动赠送金额
     */
    @Schema(description = "活动赠送金额")
    @ExcelProperty("彩金")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal activityAmount;


    /**
     * 流水倍数
     */
    @Schema(description = "流水倍数")
    @ExcelProperty("流水倍数")
    private BigDecimal runningWaterMultiple;

    /**
     * 流水要求
     */
    private BigDecimal runningWater;

    @Schema(description = "派发时间")
    private Long sendTime;

    @ExcelProperty("派发时间")
    private String sendTimeStr;

    public String getSendTimeStr(){
        if(!ObjectUtils.isEmpty(this.sendTime)){
            return  DateUtils.formatDateByZoneId(this.sendTime,DateUtils.FULL_FORMAT_1, CurrReqUtils.getTimezone());
        }
        return StringUtils.EMPTY;
    }



    /**
     * 领取时间
     */
    @Schema(description = "领取时间")
    private Long receiveTime;

    @ExcelProperty("领取时间")
    private String receiveTimeStr;

    public String getReceiveTimeStr(){
        if(!ObjectUtils.isEmpty(this.receiveTime)){
            return  DateUtils.formatDateByZoneId(this.receiveTime,DateUtils.FULL_FORMAT_1, CurrReqUtils.getTimezone());
        }
        return StringUtils.EMPTY;
    }

    /**
     * 可领取开始时间
     */
    @Schema(description = "可领取开始时间")
    private Long receiveStartTime;
    /**
     * 可领取结束时间
     */
    @Schema(description = "可领取结束时间")
    private Long receiveEndTime;

    @ExcelProperty("过期时间")
    private String receiveEndTimeStr;

    public String getReceiveEndTimeStr(){
        if(!ObjectUtils.isEmpty(this.receiveEndTime)){
            return  DateUtils.formatDateByZoneId(this.receiveEndTime,DateUtils.FULL_FORMAT_1, CurrReqUtils.getTimezone());
        }
        return StringUtils.EMPTY;
    }

}
