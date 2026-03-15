package com.cloud.baowang.admin.vo.export;

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
@Schema(description = "总台-会员列表 导出返回")
@ExcelIgnoreUnannotated
@I18nClass
public class AdminUserInfoExportVO {


    @Schema(description = "注册时间")
    private Long registerTime;
    @Schema(description = "注册时间")
    @ExcelProperty("注册时间")
    @ColumnWidth(10)
    private String registerTimeStr;

    public String getRegisterTimeStr() {
        return registerTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(registerTime, CurrReqUtils.getTimezone());
    }

    @Schema(description = "站点编号")
    @ExcelProperty("站点编号")
    private String siteCode;

    @Schema(description = "站点名称")
    @ExcelProperty("站点名称")
    private String siteName;


    @Schema(description = "会员ID")
    @ExcelProperty(value = "会员ID")
    @ColumnWidth(25)
    private String userId;
    @Schema(description = "会员账号")
    @ExcelProperty(value = "会员账号")
    @ColumnWidth(30)
    private String userAccount;

    @Schema(description = "账号类型 1测试 2正式")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE)
    private Integer accountType;
    @Schema(description = "账号类型 1测试 2正式 - Text")
    @ExcelProperty(value = "账号类型")
    @ColumnWidth(20)
    private String accountTypeText;



    @Schema(description = "风控层级id")
    private String riskLevelId;

    @Schema(description = "风控层级")
    @ExcelProperty(value = "风控层级")
    private String riskLevel;


    @Schema(description = "钱包余额")
    @ExcelProperty(value = "钱包余额")
    @ColumnWidth(20)
    private BigDecimal centerWalletAmount;

    @Schema(description = "平台币钱包余额")
    @ExcelProperty(value = "平台币钱包余额")
    @ColumnWidth(20)
    private BigDecimal platAmount;

    @Schema(description = "盘口模式")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.COMMISSION_HANDICAP_MODE)
    private Integer handicapMode;

    @Schema(description = "0:国际盘 1:大陆盘")
    @ExcelProperty(value = "盘口模式")
    private String handicapModeText;



    @Schema(description = "主货币")
    @ExcelProperty(value = "主货币")
    @ColumnWidth(20)
    private String mainCurrency;


    @Schema(description = "vip段位")
    @ExcelProperty(value = "VIP段位")
    @ColumnWidth(15)
    @I18nField
    private String vipRankName;

    @Schema(description = "vip等级名称")
    @ExcelProperty(value = "VIP等级")
    private String vipGradeName;

    @Schema(description = "会员标签")
    @ExcelProperty(value = "标签")
    @ColumnWidth(30)
    private String userLabel;

    @Schema(description = "上级代理ID")
    @ExcelProperty(value = "上级代理")
    @ColumnWidth(25)
    private String superAgentAccount;

    @Schema(description = "账号状态 1正常 2登录锁定 3游戏锁定 4充提锁定")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_STATUS)
    private String accountStatus;
    @Schema(description = "账号状态 1正常 2登录锁定 3游戏锁定 4充提锁定 - Text")
    @ExcelProperty(value = "账号状态")
    @ColumnWidth(20)
    private String accountStatusText;

    @Schema(description = "在线状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ONLINE_STATUS)
    private Integer onlineStatus;

    @Schema(description = "会员状态 1在线 2离线")
    @ExcelProperty(value = "在线状态")
    @ColumnWidth(5)
    private String onlineStatusText;

    @Schema(description = "离线天数")
    @ExcelProperty(value = "离线天数")
    private Integer offlineDays;


    @Schema(description = "邀请码")
    @ExcelProperty(value = "邀请码")
    @ColumnWidth(10)
    private String friendInviteCode;

    @Schema(description = "首存金额")
    @ExcelProperty(value = "首存金额")
    @ColumnWidth(20)
    private BigDecimal firstDepositAmount;

    @Schema(description = "注册ip")
    @ExcelProperty(value = "注册ip")
    @ColumnWidth(10)
    private String registerIp;

    @Schema(description = "注册IP归属地")
    @ExcelProperty(value = "注册IP归属地")
    @ColumnWidth(10)
    private String registerIpAttribution;


    @Schema(description = "注册终端")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REGISTRY)
    private Integer registry;
    @Schema(description = "注册终端 - Text")
    @ExcelProperty(value = "注册终端")
    @ColumnWidth(20)
    private String registryText;

    @Schema(description = "最后登录ip")
    @ExcelProperty(value = "最后登录ip")
    @ColumnWidth(15)
    private String lastLoginIp;

    @Schema(description = "首存时间")
    private Long firstDepositTime;

    @Schema(description = "首存时间 - 用于导出")
    @ExcelProperty(value = "首存时间")
    @ColumnWidth(25)
    private String firstDepositTimeStr;

    @Schema(description = "ETH热钱包地址")
    @ExcelProperty(value = "ETH热钱包地址")
    @ColumnWidth(25)
    private String ethAddress;

    @Schema(description = "Tron热钱包地址")
    @ExcelProperty(value = "Tron热钱包地址")
    @ColumnWidth(25)
    private String tronAddress;

    public String getFirstDepositTimeStr() {
        return firstDepositTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(firstDepositTime, CurrReqUtils.getTimezone());
    }

    @Schema(description = "最后登录时间")
    private Long lastLoginTime;

    @Schema(description = "最后登录时间 - 用于导出")
    @ExcelProperty(value = "最后登录时间")
    @ColumnWidth(25)
    private String lastLoginTimeStr;

    public String getLastLoginTimeStr() {
        return lastLoginTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(lastLoginTime, CurrReqUtils.getTimezone());
    }




}
