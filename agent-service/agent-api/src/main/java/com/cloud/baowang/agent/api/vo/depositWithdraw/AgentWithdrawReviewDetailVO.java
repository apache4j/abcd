package com.cloud.baowang.agent.api.vo.depositWithdraw;

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
public class AgentWithdrawReviewDetailVO {

    @Schema(description = "订单号")
    private String orderNo;

    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEVICE_TYPE)
    private String deviceType;

    @Schema(description = "订单来源")
    private String deviceTypeText;

    @Schema(description = "提款设备终端")
    private String deviceNo;

    @Schema(description = "提款IP")
    private String applyIp;

    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.YES_NO)
    private String isFirstOut;

    @Schema(description = "是否为首提")
    private String isFirstOutText;

    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.YES_NO)
    private String isBigMoney;

    @Schema(description = "是否为大额提款")
    private String isBigMoneyText;

    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.YES_NO)
    private String isContinue;

    @Schema(description = "是否连续提款")
    private String isContinueText;

    @Schema(description = "提款币种")
    private String currencyCode;

    @Schema(description = "今日累计提款次数")
    private Integer todayWithdrawNum;

    @Schema(description = "单日免费提款总次数")
    private Integer dailyFreeCount;

    @Schema(description = "今日累计提款总额+currencyCode")
    private BigDecimal todayWithdrawAmount;

    @Schema(description = "单日免费提款总额+currencyCode")
    private BigDecimal dailyFreeWithdrawalTotalAmount;

    @Schema(description = "提款方式")
    @I18nField
    private String depositWithdrawWay;

    @Schema(description = "汇率")
    private BigDecimal exchangeRate;

    @Schema(description = "提款方式信息")
    private String withdrawInfo;

    @Schema(description = "提款申请金额+currencyCode")
    private BigDecimal applyAmount;

    @Schema(description = "手续费+currencyCode")
    private BigDecimal feeAmount;

    @Schema(description = "实际到账金额+currencyCode")
    private String tradeCurrencyAmount;

    @Schema(description = "账变金额+platCurrencyCode")
    private BigDecimal arriveAmount;

    @Schema(description = "提款剩余金额+platCurrencyCode")
    private BigDecimal remainingAmount;

    @Schema(description = "平台币代码")
    private String platCurrencyCode;

    @Schema(description = "IFSC码(印度)")
    private String ifscCode;

    @Schema(description = "CPF")
    private String cpf;
}
