package com.cloud.baowang.user.api.vo.user.excel;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@ExcelIgnoreUnannotated
@Schema(description = "会员注册信息下载excel 查询返回对象")
@I18nClass
public class UserRegistrationInfoExportVO implements Serializable {

    @Schema(description = "注册时间")
    //@ExcelProperty("注册时间")
    //@ColumnWidth(25)
    private Long registrationTime;

    @Schema(title = "注册时间")
    @ExcelProperty(value = "注册时间")
    @ColumnWidth(25)
    private String registrationTimeStr;

    public String getRegistrationTimeStr() {
        return TimeZoneUtils.formatTimestampToTimeZone(registrationTime, CurrReqUtils.getTimezone());
    }

    /*@Schema(title = "会员Id")
    @ExcelProperty("会员Id")
    @ColumnWidth(25)
    private String memberId;*/

    @Schema(description = "账号类型 1测试 2正式 - Text")
    @ExcelProperty(value = "账号类型")
    @ColumnWidth(25)
    private String memberTypeText;

    @Schema(description = "账号类型 1测试 2正式")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE)
    private String memberType;

    @Schema(description = "会员账号")
    @ExcelProperty(value = "会员账号")
    @ColumnWidth(25)
    private String memberAccount;


    @Schema(description = "主币种")
    @ExcelProperty(value = "主币种")
    @ColumnWidth(25)
    private String mainCurrency;

    @Schema(description = "上级代理")
    @ExcelProperty(value = "上级代理")
    @ColumnWidth(25)
    private String superiorAgent;

    @Schema(description = "区号")
    @ExcelProperty(value = "区号")
    @ColumnWidth(30)
    private String areaCode;
    @Schema(description = "手机号码")
    @ExcelProperty(value = "注册手机号码")
    @ColumnWidth(30)
    private String phone;

    @Schema(description = "邮箱")
    @ExcelProperty(value = "注册邮箱")
    @ColumnWidth(30)
    private String email;

    @Schema(description = "注册IP")
    @ExcelProperty(value = "注册IP")
    @ColumnWidth(30)
    private String registerIp;

    @Schema(description = "注册IP风控层级")
    @ExcelProperty(value = "注册IP风控层级")
    @ColumnWidth(30)
    private String registerIpLevel;


    @Schema(description = "IP归属地")
    @ExcelProperty(value = "IP归属地")
    @ColumnWidth(25)
    private String ipAttribution;


    @Schema(description = "注册终端")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REGISTRY)
    private String registerTerminal;

    @Schema(description = "注册终端 - Text")
    @ExcelProperty(value = "注册终端")
    @ColumnWidth(25)
    private String registerTerminalText;


    @Schema(description = "终端设备号")
    @ExcelProperty(value = "终端设备号")
    @ColumnWidth(25)
    private String terminalDeviceNumber;


    @Schema(description = "终端设备号 风控层级")
    @ExcelProperty(value = "终端设备号风控层级")
    @ColumnWidth(25)
    private String terminalDeviceNumberLevel;


}
