package com.cloud.baowang.site.vo.export;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: kimi
 */
@Data
@Schema(description = "会员免费旋转 导出返回")
@ExcelIgnoreUnannotated
@I18nClass
public class UserInFreeGameExportVO {

    @ExcelProperty(value = "订单号")
    @Schema(description = "获取来源订单号 唯一值 做防重处理; 活动订单号|注单订单号")
    private String orderNo;

    @ExcelProperty(value = "会员账号")
    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "平台code")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VENUE_CODE)
    private String venueCode;

    @Schema(description = "游戏场馆")
    @ExcelProperty(value = "游戏场馆")
    private String venueCodeText;

    @Schema(description = "游戏名称")
    @I18nField
    @ExcelProperty(value = "游戏名称")
    private String gameName;

    @ExcelProperty(value = "赠送次数")
    @Schema(description = "赠送次数")
    private Integer acquireNum;

    @Schema(description = "消耗次数")
    @ExcelProperty(value = "消耗次数")
    private Integer consumeCount;

    @ExcelProperty(value = "时效")
    @Schema(title = "时效")
    private Integer timeLimit;

    /**
     * 派彩金额
     */
    @Schema(description = "派彩金额")
    @ExcelProperty(value = "派彩金额")
    private BigDecimal betWinLose;

    @Schema(description = "币种")
    @ExcelProperty(value = "币种")
    private String currencyCode;

    @Schema(description = "创建时间 ")
    private Long createdTime;

    @ExcelProperty(value = "操作时间")
    @ColumnWidth(20)
    private String createdTimeStr;

    public String getCreatedTimeStr() {
        return createdTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(createdTime, CurrReqUtils.getTimezone());
    }

    @ExcelProperty(value = "最近操作人")
    @ColumnWidth(20)
    private String creator;


}
