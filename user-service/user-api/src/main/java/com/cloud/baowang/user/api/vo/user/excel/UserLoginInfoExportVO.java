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
import com.cloud.baowang.user.api.vo.GetUserLabelByIdsResponseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author 小智
 * @Date 15/5/23 10:34 AM
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "会员详情用户登录日志信息下载excel")
@I18nClass
@ExcelIgnoreUnannotated
public class UserLoginInfoExportVO implements Serializable {

    @Schema(description = "登录时间")
    @ExcelProperty(value = "登录时间")
    @ColumnWidth(25)
    private String loginTimeStr;


    /* 登录状态 */
    @Schema(description = "登录状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.LOGIN_TYPE)
    private Integer loginType;

    @Schema(description = "登录状态")
    @ExcelProperty(value = "登录状态")
    @ColumnWidth(25)
    private String loginTypeText;


    /* 会员账号 */
    @Schema(description = "会员账号")
    @ExcelProperty(value = "会员账号")
    @ColumnWidth(25)
    private String userAccount;

    @Schema(description = "所属代理")
    @ExcelProperty(value = "所属代理")
    @ColumnWidth(25)
    private String superAgentAccount;

    /* 账号类型 */
    @Schema(description = "账号类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE)
    private String accountType;

    @Schema(title = "账号类型 1测试 2正式")
    @ExcelProperty(value = "账号类型")
    @ColumnWidth(25)
    private String accountTypeText;

    @Schema(description = "会员标签列表")
    private List<GetUserLabelByIdsResponseVO> labelList;

    @Schema(description = "会员标签列表")
    @ExcelProperty(value = "会员标签")
    private String labelListText;
    public String getLabelListText() {
        if (labelList == null || labelList.isEmpty()) {
            return "";
        }
        return String.join(",", labelList.stream().map(GetUserLabelByIdsResponseVO::getLabelName).collect(Collectors.toList()));
    }

    /* ip */
    @Schema(description = "ip")
    @ExcelProperty(value = "登录IP")
    @ColumnWidth(25)
    private String ip;


    /* ip风控层级 */
    @Schema(description = "登录IP风控层级")
    @ExcelProperty(value = "登录IP风控层级")
    @ColumnWidth(25)
    private String ipControl;


    /* ip归属地 */
    @Schema(description = "ip归属地")
    @ExcelProperty(value = "ip归属地")
    @ColumnWidth(25)
    private String ipAddress;


    /* 登录终端 */
    @Schema(description = "登录终端")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEVICE_TERMINAL)
    private String loginTerminal;

    /* 登录终端名称 */
    @Schema(description = "登录终端")
    @ExcelProperty(value = "登录终端")
    @ColumnWidth(25)
    private String loginTerminalText;


    /* 终端设备号  */
    @Schema(description = "终端设备号 ")
    @ExcelProperty(value = "终端设备号")
    @ColumnWidth(25)
    private String deviceNo;


    /* 端设备号风控层级 */
    @Schema(description = "终端设备号风控层级")
    @ExcelProperty(value = "终端设备号风控层级")
    @ColumnWidth(25)
    private String deviceControl;

    /* 登录网址 */
    @Schema(description = "登录网址")
    @ExcelProperty(value = "登录网址")
    @ColumnWidth(25)
    private String loginAddress;


    /* 设备版本 */
    @Schema(description = "设备类型")
    @ExcelProperty(value = "设备类型")
    @ColumnWidth(25)
    private String deviceVersion;

    /* 登录时间 */
    @Schema(description = "登录时间")
    private Long loginTime;


    @Schema(description = "版本号")
    @ExcelProperty(value = "版本号")
    @ColumnWidth(25)
    private String version;


    /* 备注 */
    @Schema(description = "备注")
    @ExcelProperty(value = "备注")
    @ColumnWidth(25)
    private String remark;

    @Schema(title = "站点code")
    @ColumnWidth(25)
    private String siteCode;

    public String getLoginTimeStr() {
        return TimeZoneUtils.formatTimestampToTimeZone(loginTime, CurrReqUtils.getTimezone());
    }
}
