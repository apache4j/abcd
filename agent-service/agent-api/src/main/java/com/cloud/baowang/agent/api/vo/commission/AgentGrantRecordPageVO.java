package com.cloud.baowang.agent.api.vo.commission;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.cloud.baowang.agent.api.enums.commission.CommissionTypeEnum;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.BaseVO;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/11/08 19:57
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ExcelIgnoreUnannotated
@I18nClass
@Schema(description ="代理佣金发放记录分页对象")
public class AgentGrantRecordPageVO extends BaseVO implements Serializable {
    @Schema(description ="代理账号")
    @ExcelProperty(value = "代理账号", order = 2)
    @ColumnWidth(25)
    private String agentAccount;
    @Schema(description ="代理id")
    private String agentId;
    @Schema(description ="代理层级")
    @ExcelProperty(value = "代理层级", order = 3)
    @ColumnWidth(25)
    private Integer agentLevel;

    @Schema(description ="代理类型")
    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.AGENT_TYPE)
    private Integer agentType;

    @Schema(description ="代理类型名字")
    @ExcelProperty(value = "代理类型", order = 4)
    @ColumnWidth(25)
    private String agentTypeText;

    @Schema(description = "代理类别")
    @I18nField(type= I18nFieldTypeConstants.DICT,value = CommonConstant.AGENT_CATEGORY)
    private Integer agentCategory;

    @Schema(description = "代理类别")
    @ExcelProperty(value = "代理类别", order = 6)
    @ColumnWidth(25)
    private String agentCategoryText;

    @Schema(description = "风控层级")
    @ExcelProperty(value = "风控层级", order = 7)
    @ColumnWidth(25)
    private String riskLevel;

    @Schema(description = "代理标签id")
    private String agentLabelId;

    @Schema(description = "代理标签文本")
    @ExcelProperty(value = "代理标签", order = 8)
    @ColumnWidth(25)
    private String agentLabelText;

    /**
     * 所属商务账号
     */
    @Schema(description = "所属商务账号")
    private String merchantAccount;

    /**
     * 所属商务名称
     */
    @Schema(description = "所属商务名称")
    @ExcelProperty(value = "商务名称", order = 9)
    private String merchantName;

    @Schema(description = "注册时间")
    private Long registerTime;

    @Schema(description ="注册时间--导出")
    @ExcelProperty(value = "注册时间", order = 10)
    @ColumnWidth(25)
    private String registerText;

    @Schema(description ="抽成方案")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.COMMISSION_PLAN)
    @ColumnWidth(25)
    private String commissionPlan;

    @Schema(description = "抽成方案")
    @ExcelProperty(value = "抽成方案", order = 11)
    @ColumnWidth(25)
    private String commissionPlanText;

    @Schema(description ="佣金方案id")
    private String planId;
    @Schema(description ="佣金方案名称")
    @ExcelProperty(value = "佣金方案", order = 12)
    @ColumnWidth(25)
    private String planName;




    /** {@link CommissionTypeEnum}*/
    @Schema(description = "佣金类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.COMMISSION_TYPE)
    private String commissionType;

    @Schema(description = "佣金类型名称")
    @ExcelProperty(value = "佣金类型", order = 13)
    @ColumnWidth(25)
    private String commissionTypeText;

    @Schema(description ="币种")
    private String currency;

    @Schema(description ="币种名称")
    @ExcelProperty(value = "币种", order = 14)
    @ColumnWidth(25)
    private String currencyName;

    /**结算周期  1 自然日 2 自然周  3 自然月*/
    @Schema(description = "结算周期")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SETTLE_CYCLE)
    private Integer settleCycle;

    @Schema(description = "结算周期名称")
    @ExcelProperty(value = "结算周期", order = 15)
    @ColumnWidth(25)
    private String settleCycleText;

    @Schema(description ="发放开始日期")
    private Long startTime;

    @Schema(description ="发放结束日期")
    private Long endTime;

    @Schema(description ="结算金额")
    @ExcelProperty(value = "结算金额", order = 16)
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal commissionAmount;


    @Schema(description ="发放日期")
    @ExcelProperty(value = "发放日期", order = 1)
    @ColumnWidth(25)
    private String grantDate;


    @Schema(description ="发放时间戳")
    private Long grantTime;




  /*  public String getGrantTime() {
        String start = TimeZoneUtils.formatTimestampToTimeZoneYyyyMMdd(startTime, CurrReqUtils.getTimezone());
        String end = TimeZoneUtils.formatTimestampToTimeZoneYyyyMMdd(endTime, CurrReqUtils.getTimezone());

        return start + "-" + end;
    }*/

    public String getGrantDate() {
        return DateUtils.formatDateByZoneId(grantTime, DateUtils.FULL_FORMAT_1,CurrReqUtils.getTimezone());

    }

    public String getRegisterText() {
        return TimeZoneUtils.formatTimestampToTimeZoneYyyyMMdd(registerTime, CurrReqUtils.getTimezone());
    }

}
