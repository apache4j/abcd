package com.cloud.baowang.site.vo.export;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Data
@I18nClass
@ExcelIgnoreUnannotated
@Schema(title = "会员标签变更记录VO")
public class UserLabelRecordExportVO {

    @Schema(description = "变更时间")
    private Long updatedTime;

    @ExcelProperty(value = "变更时间")
    @Schema(description = "变更时间")
    private String updatedTimeStr;

    @ExcelProperty(value = "变更前")
    @Schema(description = "变更前")
    private String beforeChange;

    @ExcelProperty(value = "变更后")
    @Schema(description = "变更后")
    private String afterChange;

    @ExcelProperty(value = "会员账号")
    @Schema(description = "会员账号")
    private String memberAccount;

    @Schema(description = "账号类型code")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE)
    private String accountType;

    @ExcelProperty(value = "账号类型")
    @Schema(description = "账号类型名称")
    private String accountTypeText;

    @Schema(description = "变更类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_CHANGE_TYPE)
    private String changeType;


    @Schema(description = "变更类型名称")
    private String changeTypeText;

    @ExcelProperty(value = "风控层级")
    @Schema(description = "风控层级")
    private String riskControlLevel;


    @Schema(description = "账号状态")
    private String accountStatus;

    @Schema(description = "账号状态名称")
    private List<CodeValueVO> accountStatusName$Arr;

    @ExcelProperty(value = "账号状态", index = 6)
    @Schema(description = "账号状态名称")
    private String accountStatusName$ArrStr;

    public String getAccountStatusName$ArrStr() {
        if (StringUtils.isNotBlank(accountStatus)) {
            return I18nMessageUtil.getSystemParamAndTrans(CommonConstant.USER_ACCOUNT_STATUS, accountStatus);
        }
        return "";
    }

    @ExcelProperty(value = "操作人", index = 7)
    @Schema(description = "操作人名称")
    private String operator;


    public String getUpdatedTimeStr() {
        return TimeZoneUtils.formatTimestampToTimeZone(updatedTime, CurrReqUtils.getTimezone());
    }

}
