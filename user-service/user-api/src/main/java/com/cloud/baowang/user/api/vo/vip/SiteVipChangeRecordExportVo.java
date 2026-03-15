package com.cloud.baowang.user.api.vo.vip;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.user.api.vo.userlabel.GetUserLabelByIdsVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * aomiao
 */
@Data
@Schema(description = "vip变更记录-导出视图")
@ExcelIgnoreUnannotated
@I18nClass
public class SiteVipChangeRecordExportVo implements Serializable {
    @Schema(description = "变更时间")
    private Long changeTime;

    @ExcelProperty("变更时间")
    @ColumnWidth(15)
    private String changeTimeStr;

    public String getChangeTimeStr() {
        return changeTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(changeTime, CurrReqUtils.getTimezone());
    }

    @Schema(description = "变更类型code")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VIP_LEVEL_CHANGE_TYPE)
    private String changeType;

    @Schema(description = "变更类型名称")
    @ColumnWidth(10)
    @ExcelProperty("变更类型")
    private String changeTypeText;


    @Schema(description = "会员账号")
    @ExcelProperty("会员账号")
    @ColumnWidth(10)
    private String userAccount;

    @Schema(description = "账号类型code")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE)
    private String accountType;

    @Schema(description = "账号类型名称")
    @ColumnWidth(10)
    @ExcelProperty("账号类型")
    private String accountTypeText;

    @Schema(description = "变更前vip等级")
    @ColumnWidth(5)
    @ExcelProperty("变更前vip等级")
    private String beforeChange;

    @Schema(description = "变更后vip等级")
    @ColumnWidth(5)
    @ExcelProperty("变更后vip等级")
    private String afterChange;

    @Schema(description = "用户标签value 用于导出")
    @ColumnWidth(50)
    @ExcelProperty("标签")
    private String userLabelName;

    @Schema(description = "风控等级")
    @ColumnWidth(30)
    @ExcelProperty("风控层级")
    private String controlRank;

    @Schema(description = "账号状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_STATUS)
    private String accountStatus;

    @Schema(description = "账号状态")
    @ExcelProperty("账号状态")
    @ColumnWidth(10)
    private String accountStatusText;

    @Schema(description = "操作人")
    @ExcelProperty("操作人")
    @ColumnWidth(10)
    private String operator;

    @Schema(description = "用户标签ids")
    private String userLabel;

    @Schema(description = "用户标签key value数组")
    private List<GetUserLabelByIdsVO> userLabelByIdsVOS;




}
