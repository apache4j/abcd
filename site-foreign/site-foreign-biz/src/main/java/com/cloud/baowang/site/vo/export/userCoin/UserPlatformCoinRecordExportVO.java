package com.cloud.baowang.site.vo.export.userCoin;

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
 * @author qiqi
 */
@Schema(title ="会员平台币账变导出对象")
@Data
@ExcelIgnoreUnannotated
@I18nClass
public class UserPlatformCoinRecordExportVO {


    /**
     * 关联订单号
     */
    @Schema(description="关联订单号")
    @ExcelProperty("关联订单号")
    @ColumnWidth(20)
    private String orderNo;


    @Schema(description="会员ID")
    @ExcelProperty("会员ID")
    @ColumnWidth(20)
    private String userAccount;

    @Schema(description = "会员注册信息")
    @ExcelProperty("会员注册信息")
    @ColumnWidth(20)
    private String userRegister;

    /**
     * 代理名称
     */
    @Schema(description="上级代理")
    @ExcelProperty("上级代理")
    @ColumnWidth(20)
    private String agentName;

    @Schema(description="风控级别")
    @ExcelProperty("风控级别")
    @ColumnWidth(20)
    private String riskControlLevel;

    @Schema(description="会员标签")
    @ExcelProperty("会员标签")
    @ColumnWidth(20)
    private String userLabel;


    /**
     * VIP等级
     */
    @Schema(description="VIP等级名称")
    @ExcelProperty("VIP等级")
    @ColumnWidth(20)
    private String vipGradeCodeName;

    /**
     * VIP段位段位名称
     */
    @Schema(description="VIP段位段位名称")
    @ExcelProperty("VIP段位")
    @ColumnWidth(20)
    private String vipRankName;

    @Schema(description="账号状态")
    @ExcelProperty("账号状态")
    @ColumnWidth(20)
    private String accountStatusText;



    @Schema(description="业务类型")
    @ExcelProperty("业务类型")
    @ColumnWidth(20)
    private String businessCoinTypeText;


    @Schema(description="账变类型")
    @ExcelProperty("账变类型")
    @ColumnWidth(20)
    private String coinTypeText;


    @Schema(description="收支类型")
    @ExcelProperty("收支类型")
    @ColumnWidth(20)
    private String balanceTypeText;

    /**
     * 账号状态
     */
    @Schema(description="账号状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_STATUS)
    private String accountStatus;

    /**
     */
    @Schema(description="业务类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.BUSINESS_COIN_TYPE)
    private String businessCoinType;

    /**
     */
    @Schema(description="账变类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.COIN_TYPE)
    private String coinType;

    /**
     * 收支类型1收入,2支出 3冻结 4 解冻
     */
    @Schema(description="收支类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.COIN_BALANCE_TYPE)
    private String balanceType;

    /**
     * 账变前金额
     */
    @Schema(description="账变前余额")
    @ExcelProperty("账变前余额")
    @ColumnWidth(20)
    private BigDecimal coinFrom;

    /**
     * 金额改变数量
     */
    @Schema(description="账变金额")
    @ExcelProperty("账变金额")
    @ColumnWidth(20)
    private BigDecimal coinValue;
    /**
     * 账变后金额
     */
    @Schema(description="账变后余额")
    @ExcelProperty("账变后余额")
    @ColumnWidth(20)
    private BigDecimal coinTo;

    @Schema(description="账变时间")
    private Long createdTime;

    @Schema(description="账变时间")
    @ExcelProperty("账变时间")
    @ColumnWidth(20)
    private String createdTimeStr;

    public String getCreatedTimeStr(){
        return createdTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(createdTime, CurrReqUtils.getTimezone());
    }
    /**
     * 备注
     */
    @Schema(description="备注")
    @ExcelProperty("备注")
    @ColumnWidth(20)
    private String remark;



}
