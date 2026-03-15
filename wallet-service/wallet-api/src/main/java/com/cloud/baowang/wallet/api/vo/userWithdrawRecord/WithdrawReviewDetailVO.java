package com.cloud.baowang.wallet.api.vo.userWithdrawRecord;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 本次提款详情
 *
 * @author qiqi
 */
@Data
@Accessors(chain = true)
@I18nClass
@Schema(title = "本次提款详情")
public class WithdrawReviewDetailVO {

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "订单来源")
    @I18nField
    private String deviceTypeName;

    @Schema(description = "提款终端设备号")
    private String deviceNo;

    @Schema(description = "提款IP")
    private String applyIp;

    @Schema(description = "是否首次提款")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.YES_NO)
    private String isFirstOut;

    @Schema(description = "是否首次提款名称")
    private String isFirstOutText;

    @Schema(description = "是否大额出款")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.YES_NO)
    private String isBigMoney;
    @Schema(description = "是否为大额提款名称")
    private String isBigMoneyText;

    @Schema(description = "是否连续提款")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.YES_NO)
    private String isContinue;

    @Schema(description = "是否连续提款名称")
    private String isContinueText;

    @Schema(description = "今日提款次数")
    private Integer todayWithdrawNum;

    @Schema(description = "单日提款免费总次数")
    private Integer dayWithdrawNum;

    @Schema(description = "单日提款免费总额度")
    private BigDecimal dailyWithdrawalFreeLimit;

    @Schema(description = "今日提款总额")
    private BigDecimal todayWithdrawAmount;

    @Schema(description = "提款类型")
    private String depositWithdrawType;

    @Schema(description = "提款方式")
    @I18nField
    private String depositWithdrawWay;

    @Schema(description = "汇率")
    private BigDecimal exchangeRate;

   /* @Schema(description = "手续费率")
    private BigDecimal feeRate;*/

    @Schema(description = "手续费")
    private BigDecimal feeAmount;

    @Schema(description = "提款绑定域名")
    private String applyDomain;

    @Schema(description = "提款信息")
    private String withdrawInfo;

    @Schema(description = "提款金额")
    private BigDecimal applyAmount;

    @Schema(description = "预计到账金额")
    private BigDecimal predictedAmount;

    @Schema(description = "本次提款流水")
    private BigDecimal coinValue;

    @Schema(description = "提款币种")
    private String currencyCode;

    @Schema(description = "提款币种金额")
    private BigDecimal tradeCurrencyAmount;

    @Schema(description = "'账户类型（ 银行卡为银行名称，虚拟币为币种）'")
    private String accountType;
    @Schema(description = "'账户分支（银行卡为开户行，虚拟币为链协议 如ERC20 TRC20)'")
    private String accountBranch;
    @Schema(description = "'存取款地址（银行卡账号，虚拟币地址）'")
    private String depositWithdrawAddress;
    /*@Schema(description = "'存取款名字'")
    private String depositWithdrawName;*/
    @Schema(description = "'存取款姓'")
    private String depositWithdrawSurname;
    @Schema(description = "'区号'")
    private String areaCode;
    @Schema(description = "'手机号'")
    private String telephone;
    @Schema(description = "'邮箱'")
    private String email;
    @Schema(description = "cpf")
    private String cpf;
    @Schema(description = "'地址'")
    private String address;
    @Schema(description = "'省'")
    private String province;
    @Schema(description = "'城市'")
    private String city;
    @Schema(description = "'邮政编码'")
    private String postalCode;
    @Schema(description = "'国家'")
    private String country;

    @Schema(description = "IFSC码(印度)")
    private String ifscCode;



}
