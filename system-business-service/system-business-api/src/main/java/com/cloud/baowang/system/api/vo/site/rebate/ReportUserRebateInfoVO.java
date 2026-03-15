package com.cloud.baowang.system.api.vo.site.rebate;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.serializer.AppBigDecimalJsonSerializer;
import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
@Schema(description =  "返水报表 resp")
@ExcelIgnoreUnannotated
@I18nClass
public class ReportUserRebateInfoVO implements Serializable {

    private String id;


    @Schema(title = "游戏类型: 1:体育 2:视讯 3:棋牌 4:电子,场馆类型：字典CODE：venue_type")
   // @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.VENUE_TYPE)
    private String venueType;

    @Schema(title = "游戏类型: 1:体育 2:视讯 3:棋牌 4:电子")
    @ExcelProperty("返水项目")
    private String venueTypeText;

    @Schema(description = "币种")
    @ExcelProperty("币种")
    private String currencyCode;

    @Schema(description = "发放人数")
    @ExcelProperty("发放人数")
    private Long issueNums=0L;

    @Schema(description = "发放金额")
    @ExcelProperty("发放金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal issueAmount=BigDecimal.ZERO;


    @Schema(description = "领取人数")
    @ExcelProperty("领取人数")
    private Long receiveNums=0L;


    @Schema(description = "领取金额")
    @ExcelProperty("领取金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal rebateAmount=BigDecimal.ZERO;

    @Schema(description = "日期")
    private String issueTime;






}
