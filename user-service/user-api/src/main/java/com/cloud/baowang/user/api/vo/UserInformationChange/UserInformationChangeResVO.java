package com.cloud.baowang.user.api.vo.UserInformationChange;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "会员信息变更记录查询返回对象")
@I18nClass
@ExcelIgnoreUnannotated
public class UserInformationChangeResVO {

    @Schema(description = "操作时间")
    private Long operatingTime;
    @ExcelProperty(value = "操作时间")
    @Schema(description = "操作时间")
    private String operatingTimeStr;

    public String getOperatingTimeStr() {
        return TimeZoneUtils.formatTimestampToTimeZone(operatingTime, CurrReqUtils.getTimezone());
    }

    @ExcelProperty(value = "会员账号")
    @Schema(description = "会员账号")
    private String memberAccount;

    @Schema(description = "账号类型code")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE)
    private String accountType;

    @ExcelProperty(value = "账号类型")
    @Schema(description = "账号类型name")
    private String accountTypeText;


    @ExcelProperty(value = "变更类型")
    @Schema(description = "账name")
    private String changeTypeText;




    @Schema(description = "变更类型code")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_CHANGE_TYPE)
    private String changeType;

    @Schema(description = "变更类型name")
    private String changeTypeName;



    @ExcelProperty(value = "提交信息")
    @Schema(description = "提交信息")
    private String submitInformation;

    @ExcelProperty(value = "变更前信息")
    @Schema(description = "变更前信息")
    private String InformationBeforeChange;




    @ExcelProperty(value = "变更前信息")
    @Schema(description = "变更前信息")
    private List<CodeValueVO> BeforeChange;


    @ExcelProperty(value = "变更后信息")
    @Schema(description = "变更后信息")
    private String InformationAfterChange;

    @ExcelProperty(value = "变更后信息")
    @Schema(description = "变更后信息")
    private List<CodeValueVO> AfterChange;





    @ExcelProperty(value = "操作人")
    @Schema(description = "操作人")
    private String operator;


}
