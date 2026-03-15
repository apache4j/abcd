package com.cloud.baowang.site.vo.export.vip;

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

import java.io.Serializable;

@Schema(description = "vip等级变更记录导出对象")
@I18nClass
@ExcelIgnoreUnannotated
@Data
public class SiteVipCnChangeRecordExcelVO implements Serializable {
    @Schema(description = "变更时间")
    private Long createdTime;

    @Schema(description = "变更时间yyyy-mm-dd hh:mi:ss")
    @ExcelProperty("变更时间")
    @ColumnWidth(20)
    private String createdTimeStr;

    public String getCreatedTimeStr() {
        return null == createdTime ? "" : TimeZoneUtils.formatTimestampToTimeZone(createdTime, CurrReqUtils.getTimezone());
    }
    @Schema(description = "升降级标识(0:升级,1:降级")
    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.VIP_LEVEL_CHANGE_TYPE)
    private Integer changeType;

    @Schema(description = "变更类型")
    @ExcelProperty("变更类型")
    @ColumnWidth(20)
    private String changeTypeText;

    @Schema(description = "会员账号")
    @ExcelProperty("会员账号")
    @ColumnWidth(10)
    private String userAccount;

    @Schema(description = "账号类型code")
    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.USER_ACCOUNT_TYPE)
    private String accountType;

    @Schema(description = "账号类型名称")
    @ExcelProperty("账号类型")
    @ColumnWidth(10)
    private String accountTypeText;

    @Schema(description = "变更前vip段位")
    @ExcelProperty("变更前vip等级")
    @ColumnWidth(5)
    private String vipOldName;

    @Schema(description = "变更后vip段位")
    @ExcelProperty("变更后vip等级")
    @ColumnWidth(5)
    private String vipNowName;

    @Schema(description = "用户标签value 用于导出")
    @ExcelProperty("标签")
    @ColumnWidth(50)
    private String userLabel;

    @Schema(description = "风控等级")
    @ExcelProperty("风控层级")
    @ColumnWidth(10)
    private String userRiskLevel;

    @Schema(description = "账号状态")
    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.USER_ACCOUNT_STATUS)
    private String accountStatus;

    @Schema(description = "账号状态")
    @ExcelProperty("账号状态")
    @ColumnWidth(10)
    private String accountStatusText;

    @Schema(description = "操作人")
    @ExcelProperty("操作人")
    @ColumnWidth(5)
    private String creator;


}
