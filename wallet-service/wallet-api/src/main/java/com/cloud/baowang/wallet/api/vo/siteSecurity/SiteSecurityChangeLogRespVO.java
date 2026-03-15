package com.cloud.baowang.wallet.api.vo.siteSecurity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
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
 * @Desciption:
 * @Author: Ford
 * @Date: 2025/6/27 19:23
 * @Version: V1.0
 **/
@Data
@Schema(description = "保证金账变记录查询结果")
@I18nClass
@ExcelIgnoreUnannotated
public class SiteSecurityChangeLogRespVO {
    /**
     * 来源订单号
     */
    @Schema(description =  "来源订单号")
    @ExcelProperty("订单号")
    private String sourceOrderNo;


    @Schema(description =  "站点名称")
    @ExcelProperty("站点名称")
    private String siteName;

    @Schema(description =  "站点编码")
    @ExcelProperty("站点编码")
    private String siteCode;


    @Schema(description =  "所属公司")
    @ExcelProperty("所属公司")
    private String company;

    @Schema(description =  "站点类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SITE_TYPE)
    private Integer siteType;

    @Schema(description = "站点类型文本")
    @ExcelProperty("站点类型")
    private String siteTypeText;


    @Schema(description =  "币种")
    @ExcelProperty("币种")
    private String currency;


    @Schema(description =  "保证金类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SITE_SECURITY_BALANCE_ACCOUNT)
    private String balanceAccount;

    @Schema(description =  "保证金类型")
    @ExcelProperty("保证金类型")
    private String balanceAccountText;

    /**
     * 来源订单类型
     */
    @Schema(description =  "业务类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SECURITY_SOURCE_COIN_TYPE)
    private String sourceCoinType;

    @Schema(description = "业务类型文本")
    @ExcelProperty("业务类型")
    private String sourceCoinTypeText;

    /**
     * 账号类型: user:会员 agent:代理 site:站点
     */
    @Schema(description = "账号类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SECURITY_USER_TYPE)
    private String userType;

    @Schema(description = "账号类型文本")
    @ExcelProperty("账号类型")
    private String userTypeText;

    @Schema(description = "会员ID")
    @ExcelProperty("会员ID")
    private Long userId;

    @Schema(description = "会员名称")
    @ExcelProperty("账号名称")
    private String userName;


    @Schema(description =  "账变类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SECURITY_COIN_TYPE)
    private String coinType;

    @Schema(description = "账变类型文本")
    @ExcelProperty("账变类型")
    private String coinTypeText;

    /**
     * 收支类型 +:收入 -:支出
     */
    @Schema(description =  "收支类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SECURITY_AMOUNT_DIRECT)
    private String amountDirect;

    @Schema(description =  "收支类型文本")
    @ExcelProperty("收支类型")
    private String amountDirectText;

    /**
     * '账变前余额'
     */
    @Schema(description =  "账变前余额")
    @ExcelProperty("账变前余额")
    private BigDecimal beforeAmount;

    /**
     * '账变金额'
     */
    @Schema(description =  "账变金额")
    @ExcelProperty("账变金额")
    private BigDecimal changeAmount;

    /**
     * 账变后金额
     */
    @Schema(description =  "账变后余额")
    @ExcelProperty("账变后余额")
    private BigDecimal afterAmount;

    @ExcelProperty("账变时间")
    private String changeTimeStr;

    public String getChangeTimeStr() {
        return changeTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(changeTime, CurrReqUtils.getTimezone());
    }

    /**
     * 账变时间
     */
    @Schema(description =  "账变时间")
    private Long changeTime;

    /**
     * 备注
     */
    @Schema(description =  "备注")
    //@ExcelProperty("备注")
    private String memo;


    @Schema(description = "主键ID")
    private String id;
    @Schema(description = "创建人")
    private String creator;
    @Schema(description = "创建时间")
    private Long createdTime;
    @Schema(description = "修改人")
    private String updater;
    @Schema(description = "修改时间")
    private Long updatedTime;

    public void addBeforeAmount(BigDecimal beforeAmount) {
        this.beforeAmount=this.beforeAmount==null?BigDecimal.ZERO:this.beforeAmount;
        beforeAmount=beforeAmount==null?BigDecimal.ZERO:beforeAmount;
        this.beforeAmount=this.beforeAmount.add(beforeAmount);
    }

    public void addChangeAmount(BigDecimal changeAmount) {
        this.changeAmount=this.changeAmount==null?BigDecimal.ZERO:this.changeAmount;
        changeAmount=changeAmount==null?BigDecimal.ZERO:changeAmount;
        this.changeAmount=this.changeAmount.add(changeAmount);
    }

    public void addAfterAmount(BigDecimal afterAmount) {
        this.afterAmount=this.afterAmount==null?BigDecimal.ZERO:this.afterAmount;
        afterAmount=afterAmount==null?BigDecimal.ZERO:afterAmount;
        this.afterAmount=this.afterAmount.add(afterAmount);
    }
}
