package com.cloud.baowang.wallet.api.vo.userCoinRecord;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author qiqi
 */
@Schema(title = "代理平台币账变记录返回对象")
@Data
@I18nClass
@ExcelIgnoreUnannotated
public class AgentHotWalletAddressVO {


    @Schema(description="站点编码",hidden = true)
    private String siteCode;

    @Schema(description="代理账号")
    @ExcelProperty("代理账号")
    private String agentAccount;


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
