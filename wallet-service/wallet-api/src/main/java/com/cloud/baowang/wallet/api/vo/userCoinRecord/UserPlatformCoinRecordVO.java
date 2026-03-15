package com.cloud.baowang.wallet.api.vo.userCoinRecord;

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
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author qiqi
 */
@Schema(title = "会员平台币账变记录返回对象")
@Data
@I18nClass
@ExcelIgnoreUnannotated
public class UserPlatformCoinRecordVO {


    /**
     * 关联订单号
     */
    @Schema(description="关联订单号")
    @ExcelProperty("关联订单号")
    private String orderNo;

    @Schema(description="会员ID")
    @ExcelProperty("会员账号")
    private String userAccount;

    /**
     * 会员标签Id
     */
    @Schema(description="会员标签Id")
    private String userLabelId;


    /**
     * 会员标签
     */
    @Schema(description="会员标签")
    @ExcelProperty("会员标签")
    private String userLabel;

    /**
     * 代理ID
     */
    @Schema(description="代理ID")
    private Long  agentId;

    /**
     * 代理名称
     */
    @Schema(description="代理名称")
    @ExcelProperty("上级代理")
    private String agentName;


    @Schema(description = "会员注册信息")
    private String userRegister;




    @Schema(description="风控级别名称")
    @ExcelProperty("风控层级")
    private String riskControlLevel;

    /**
     * 账号状态
     */
    @Schema(description="账号状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_STATUS)
    private String accountStatus;


    @Schema(description = "账号状态 1正常 2登录锁定 3游戏锁定 4充提锁定 - Text")
    @ExcelProperty("账号状态")
    private String accountStatusText;

    @Schema(description = "账号类型 ")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE)
    private String accountType;

    @Schema(title = "账号类型名称")
    @ExcelProperty("账号类型")
    private String accountTypeText;



    /**
     * VIP等级
     */
    @Schema(description="VIP等级")
    private Integer vipGradeCode;

    /**
     * VIP段位段位名称
     */
    @Schema(description="VIP段位名称")
    @I18nField
    @ExcelProperty("VIP段位")
    private String vipRankName;


    /**
     * VIP等级
     */
    @Schema(description="VIP等级名称")
    @ExcelProperty("VIP等级")
    private String vipGradeCodeName;


    @Schema(description="VIP段位")
    private Integer vipRank;


    @Schema(description="业务类型名称")
    @ExcelProperty("业务类型")
    private String businessCoinTypeText;

    @Schema(description="账变类型名称")
    @ExcelProperty("账变类型")
    private String coinTypeText;


    @Schema(description = "币种")
    @ExcelProperty("币种")
    private String currency;


    @Schema(description="收支类型名称")
    @ExcelProperty("收支类型")
    private String balanceTypeText;

    @Schema(description="风控级别ID")
    private String riskControlLevelId;


    /**
     */
    @Schema(description="业务类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.PLATFORM_BUSINESS_COIN_TYPE)
    private String businessCoinType;

    /**
     */
    @Schema(description="账变类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.PLATFORM_COIN_TYPE)
    private String coinType;

    /**
     * 收支类型1收入,2支出 3冻结 4 解冻
     */
    @Schema(description="收支类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.PLATFORM_COIN_BALANCE_TYPE)
    private String balanceType;

    /**
     * 账变前金额
     */
    @Schema(description="账变前余额")
    @ExcelProperty("账变前余额")
    private BigDecimal coinFrom;

    /**
     * 金额改变数量
     */
    @Schema(description="账变金额")
    @ExcelProperty("账变金额")
    private BigDecimal coinValue;

    /**
     * 账变后金额
     */
    @Schema(description="账变后余额")
    @ExcelProperty("账变后余额")
    private BigDecimal coinTo;

    @Schema(description="账变时间;导出需要")
    @ExcelProperty("账变时间")
    private String createdTimeStr;


    /**
     * '当前金额
     */
    @Schema(description="当前金额")
    private BigDecimal coinAmount;



    /**
     * 备注
     */
    @Schema(description="备注")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description="账变时间")
    private Long createdTime;

    public String getCreatedTimeStr(){
        return createdTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(createdTime, CurrReqUtils.getTimezone());
    }
}
