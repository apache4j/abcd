package com.cloud.baowang.wallet.api.vo.siteSecurity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
@Schema(title ="保证金查询结果")
@I18nClass
@ExcelIgnoreUnannotated
public class SiteSecurityBalanceRespVO {

    @Schema(description = "站点名称")
    @ExcelProperty("站点名称")
    private String siteName;

    @Schema(description = "站点编码")
    @ExcelProperty("站点编码")
    private String siteCode;

    @Schema(description = "所属公司")
    @ExcelProperty("所属公司")
    private String company;

    @Schema(description = "站点类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SITE_TYPE)
    private Integer siteType;

    @Schema(description = "站点类型文本")
    @ExcelProperty("站点类型")
    private String siteTypeText;

    @Schema(description = "站点状态 ")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SITE_STATUS)
    private Integer siteStatus;

    @Schema(description = "站点状态文本")
    @ExcelProperty("站点状态")
    private String siteStatusText;

    @Schema(description = "保证金开启状态 0:未开启 1:已开启")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SECURITY_STATUS)
    private Integer securityStatus;

    @Schema(description = "保证金开启状态文本")
    @ExcelProperty("保证金管理状态")
    private String securityStatusText;

    @Schema(description = "币种")
    @ExcelProperty("币种")
    private String currency;

    @Schema(description = "保证金账户状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SECURITY_ACCOUNT_STATUS)
    private Integer accountStatus;

    @Schema(description = "保证金账户状态文本")
    @ExcelProperty("保证金账户状态")
    private String accountStatusText;

    @Schema(description = "保证金")
    @ExcelProperty("保证金余额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal availableBalance;


    @Schema(description = "冻结保证金")
    @ExcelProperty("冻结保证金余额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal frozenBalance;


    @Schema(description = "预警阀值")
    @ExcelProperty("保证金预警阈值")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal thresholdAmount;



    @Schema(description = "透支额度")
    @ExcelProperty("透支额度")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal overdrawAmount;



    @Schema(description = "剩余透支额度")
    @ExcelProperty("剩余透支额度")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal remainOverdraw;

    @Schema(description = "冻结透支额度")
    @ExcelProperty("冻结透支额度")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal frozenOverdraw;

    @Schema(description = "最近更新时间")
    @ExcelProperty("最近更新时间")
    private String updatedTimeStr;

    public String getUpdatedTimeStr() {
        return updatedTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(updatedTime, CurrReqUtils.getTimezone());
    }

    @Schema(description = "主键ID")
    private String id;
    @Schema(description = "创建人")
    private String creator;
    @Schema(description = "创建时间")
    private Long createdTime;
    @Schema(description = "修改人")
    @ExcelProperty("最近操作人")
    private String updater;
    @Schema(description = "修改时间")
    private Long updatedTime;


}
