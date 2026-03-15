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

import java.math.BigDecimal;

/**
 * @author qiqi
 */
@Schema(title = "会员平台币账变记录返回对象")
@Data
@I18nClass
@ExcelIgnoreUnannotated
public class UserHotWalletAddressVO {


    @Schema(description="站点编码",hidden = true)
    private String siteCode;

    @Schema(description="会员账号")
    @ExcelProperty("会员账号")
    private String userAccount;

    @Schema(description="会员Id")
    @ExcelProperty("会员Id")
    private String userId;

    @Schema(description="trc钱包地址")
    @ExcelProperty("trc钱包地址")
    private String trcAddress;

    @Schema(description="trc钱包余额")
    @ExcelProperty("trc钱包余额")
    private BigDecimal trcAddressBalance;

    @Schema(description="erc钱包地址")
    @ExcelProperty("erc钱包地址")
    private String ercAddress;

    @Schema(description="trc唯一编号 ")
    private String trcOutAddressNo;


    @Schema(description="erc唯一编号 ")
    private String ercOutAddressNo;

    @Schema(description="erc钱包余额")
    @ExcelProperty("erc钱包余额")
    private BigDecimal ercAddressBalance;
}
