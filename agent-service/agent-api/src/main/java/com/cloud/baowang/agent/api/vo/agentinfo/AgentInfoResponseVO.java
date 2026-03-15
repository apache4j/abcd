package com.cloud.baowang.agent.api.vo.agentinfo;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author: kimi
 */
@Data
@Schema(title = "代理列表 返回")
@I18nClass
@ExcelIgnoreUnannotated
public class AgentInfoResponseVO {

    @Schema(title = "代理id")
    private String id;


    @Schema(title = "代理编号")
    private String agentId;

    @Schema(title = "代理账号")
    @ExcelProperty("代理账号")
    private String agentAccount;


    @Schema(title = "邀请码")
    @ExcelProperty("邀请码")
    private String inviteCode;

   /* @Schema(title = "代理归属 1推广 2招商 3官资")
    private Integer agentAttribution;
    @Schema(title = "代理归属 1推广 2招商 3官资 - Name")
    private String agentAttributionName;*/

    @Schema(title = "代理层级")
    @ExcelProperty("代理层级")
    private Integer level;
    @Schema(title = "层级名称")
    @ExcelProperty("层级名称")
    private String levelName;

    @Schema(title = "代理层级/层级名称")
    private String level_$_levelName;

    @ExcelProperty("商务名称")
    @Schema(description = "商务名称")
    private String merchantName;

    @Schema(title = "直属上级id")
    private String parentId;
    @Schema(title = "直属上级")
    @ExcelProperty("直属上级")
    private String parentIdName;

    @Schema(title = "代理类别")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_CATEGORY)
    private Integer agentCategory;
    @Schema(title = "代理类别")
    private String agentCategoryText;

    @Schema(title = "账号类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_TYPE)
    private Integer agentType;
    @Schema(title = "账号类型")
    @ExcelProperty("账号类型")
    private String agentTypeText;

    @Schema(title = "账号状态")
    @I18nField(type = I18nFieldTypeConstants.DICT_CODE_TO_STR, value = CommonConstant.AGENT_STATUS)
    private String status;
    @Schema(title = "账号状态描述")
    @ExcelProperty("账号状态")
    private String statusText;

    @Schema(title = "风控层级id")
    private String riskLevelId;
    @Schema(title = "风控等级")
    @ExcelProperty("风控等级")
    private String riskLevel;

    @Schema(title = "标签id")
    private String agentLabelId;
    @Schema(title = "标签")
    @ExcelProperty("标签")
    private String agentLabel;

    @Schema(title = "注册方式")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.REGISTER_WAY)
    private Integer registerWay;
    @Schema(title = "注册方式")
    @ExcelProperty("注册方式")
    private String registerWayText;

    @Schema(title = "下级代理人数")
    @ExcelProperty("下级代理人数")
    private Integer downAgentNumber;

    @Schema(title = "下级会员人数")
    @ExcelProperty("下级会员人数")
    private Integer downUserNumber;

    @Schema(title = "直属代理人数")
    @ExcelProperty("直属代理人数")
    private Integer directAgentNumber;

    @Schema(title = "直属会员人数")
    @ExcelProperty("直属会员人数")
    private Integer directUserNumber;

    @Schema(title = "佣金钱包余额")
    @ExcelProperty("佣金钱包余额")
    private BigDecimal commissionWalletBalance;

    @Schema(title = "额度钱包余额")
    @ExcelProperty("额度钱包余额")
    private BigDecimal quotaWalletBalance;

    @Schema(title = "佣金钱包余额/额度钱包余额")
    private String commissionWalletBalance_$_quotaWalletBalance;

    @Schema(title = "总存款金额")
    @ExcelProperty("总存款金额")
    private BigDecimal totalDepositAmount;

    @Schema(title = "总存款次数")
    @ExcelProperty("总存款次数")
    private Integer totalDepositTimes;

    @Schema(title = "总存款金额/总存款次数")
    private String totalDepositAmount_$_totalDepositTimes;

    @Schema(title = "总提款金额")
    @ExcelProperty("总提款金额")
    private BigDecimal totalWithdrawAmount;

    @Schema(title = "总提款次数")
    @ExcelProperty("总提款次数")
    private Integer totalWithdrawTimes;

    @Schema(title = "总提款金额/总提款次数")
    private String totalWithdrawAmount_$_totalWithdrawTimes;

    @Schema(title = "最后登录时间")
    private Long lastLoginTime;
    @Schema(title = "最后登录时间 - 用于导出")
    @ExcelProperty("最后登录时间")
    private String lastLoginTimeStr;

    @Schema(title = "注册时间")
    private Long registerTime;
    @Schema(title = "注册时间 - 用于导出")
    @ExcelProperty("注册时间")
    private String registerTimeStr;

    @Schema(title = "IP白名单")
    private String agentWhiteList;

    @Schema(description = "商务账号")
    private String merchantAccount;

//    a.plan_code planCode,
//    a.plan_name planName,

    @Schema(title = "方案码")
    private String planCode;

    @ExcelProperty("方案名称")
    @Schema(description = "方案名称")
    private String planName;



    public String getLastLoginTimeStr() {
        return null == lastLoginTime ? null : DateUtils.formatDateByZoneId(lastLoginTime, DatePattern.NORM_DATETIME_PATTERN, CurrReqUtils.getTimezone());
    }

    public String getRegisterTimeStr() {
        return null == registerTime ? null : DateUtils.formatDateByZoneId(registerTime, DatePattern.NORM_DATETIME_PATTERN, CurrReqUtils.getTimezone());
    }

    public String getLevel_$_levelName() {
        return String.valueOf(this.level);
    }

    public String getCommissionWalletBalance_$_quotaWalletBalance() {
        this.commissionWalletBalance = this.commissionWalletBalance == null ? BigDecimal.ZERO : this.commissionWalletBalance;
        return String.valueOf(this.commissionWalletBalance);
    }

    public String getTotalWithdrawAmount_$_totalWithdrawTimes() {
        this.totalWithdrawAmount = this.totalWithdrawAmount == null ? BigDecimal.ZERO : this.totalWithdrawAmount;
        return String.valueOf(this.totalWithdrawAmount);
    }

    public String getTotalDepositAmount_$_totalDepositTimes() {
        this.totalDepositAmount = this.totalDepositAmount == null ? BigDecimal.ZERO : this.totalDepositAmount;
        return String.valueOf(this.totalDepositAmount);
    }
}
