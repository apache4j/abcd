package com.cloud.baowang.agent.api.vo.agentCoin;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author : 小智
 * @Date : 24/10/23 12:42 PM
 * @Version : 1.0
 */
@Data
@I18nClass
@Schema(title = "代理账变记录返回对象")
@ExcelIgnoreUnannotated
public class AgentCoinRecordVO implements Serializable {

    @Schema(description = "关联订单号")
    @ExcelProperty("订单号")
    @ColumnWidth(15)
    private String orderNo;

    @Schema(description = "代理账号")
    @ExcelProperty("代理账号")
    @ColumnWidth(15)
    private String agentAccount;

    @Schema(description = "风控级别Id")
    private String riskControlLevelId;

    @Schema(description = "风控级别")
    @ExcelProperty("风控层级")
    private String riskControlLevel;


    @Schema(description = "币种")
    private String currency;

    @Schema(description = "代理名称")
    private String agentName;

    @Schema(description = "代理ID父节点")
    private String parentId;

    @Schema(description = "层次id逗号分隔")
    private String path;

    @Schema(description = "层级")
    private Integer level;

    @Schema(description = "账号状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_STATUS)
    private String accountStatus;


    @Schema(description = "账号状态 名称")
    @ExcelProperty("账号状态")
    private String accountStatusText;

    @Schema(description = "代理钱包")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_WALLET_TYPE)
    private String walletType;


    @Schema(description = "代理钱包名称")
    @ExcelProperty("代理钱包")
    private String walletTypeText;


    @Schema(description = "账变业务类型 ")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_BUSINESS_COIN_TYPE)
    private String businessCoinType;

    @Schema(description = "账变业务类型名称 ")
    @ExcelProperty("业务类型")
    private String businessCoinTypeText;

    @Schema(description = "账变类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_COIN_TYPE)
    private String coinType;

    @Schema(description = "账变类型名称")
    @ExcelProperty("账变类型")
    private String coinTypeText;

    @Schema(description = "收支类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.COIN_BALANCE_TYPE)
    private String balanceType;

    @Schema(description = "收支类型名称")
    @ExcelProperty("收支类型")
    private String balanceTypeText;

    @Schema(description = "账变前余额")
    @ExcelProperty("账变前余额")
    private BigDecimal coinFrom;

    @Schema(description = "账变金额")
    @ExcelProperty("账变金额")
    private BigDecimal coinAmount;

    @Schema(description = "账变后余额")
    @ExcelProperty("账变后余额")
    private BigDecimal coinTo;

    @Schema(description = "账变时间")
    private Long createdTime;

    @Schema(description="账变时间")
    @ExcelProperty("账变时间")
    private String createdTimeStr;

    @Schema(description = "备注")
    @ExcelProperty("备注")
    private String remark;

    private String siteCode;

    public AgentCoinRecordVO addCoinFrom(BigDecimal _coinFrom){
        BigDecimal currentData=this.getCoinFrom()==null?BigDecimal.ZERO:this.getCoinFrom();
        BigDecimal changeData=_coinFrom==null?BigDecimal.ZERO:_coinFrom;
        this.coinFrom=currentData.add(changeData);
        return this;
    }
    public AgentCoinRecordVO addCoinTo(BigDecimal _coinTo){
        BigDecimal currentData=this.getCoinTo()==null?BigDecimal.ZERO:this.getCoinTo();
        BigDecimal changeData=_coinTo==null?BigDecimal.ZERO:_coinTo;
        this.coinTo=currentData.add(changeData);
        return this;
    }
    public AgentCoinRecordVO addCoinAmount(BigDecimal _coinAmount){
        BigDecimal currentData=this.getCoinAmount()==null?BigDecimal.ZERO:this.getCoinAmount();
        BigDecimal changeData=_coinAmount==null?BigDecimal.ZERO:_coinAmount;
        this.coinAmount=currentData.add(changeData);
        return this;
    }

}
