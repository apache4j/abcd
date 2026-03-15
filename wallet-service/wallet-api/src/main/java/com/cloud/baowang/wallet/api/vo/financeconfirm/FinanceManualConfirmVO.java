package com.cloud.baowang.wallet.api.vo.financeconfirm;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@I18nClass
@Data
@Schema(title ="会员提款人工确认VO")
public class FinanceManualConfirmVO {

    @Schema(title = "id")
    private String id;

    @Schema(title = "审核员")
    private String auditUser;

    @Schema(title = "订单号")
    private String orderNo;

    @Schema(title = "会员ID")
    private String userAccount;

    @Schema(title = "会员注册信息")
    private String userRegister;

    @Schema(title = "提款金额")
    private BigDecimal applyAmount;

    @Schema(title = "账户类型（ 银行卡为银行名称，虚拟币为币种）")
    private String accountType;

    @Schema(title = "账户类型（ 银行卡为银行名称，虚拟币为币种）")
    private String accountBranch;

    @Schema(title = "存取款地址")
    private String depositWithdrawAddress;

    @Schema(title = "存取款用户名")
    private String depositWithdrawName;

    @Schema(title = "提款类型")
    private String depositWithdrawType;

    @Schema(title = "提款方式")
    private String depositWithdrawMethod;

    @Schema(title = "提款币种")
    private String currency;

    @Schema(title = "交易币种金额")
    private BigDecimal tradeCurrencyAmount;

    @Schema(title = "汇率")
    private BigDecimal exchangeRate;

    @Schema(title = "申请时间")
    private Long applyTime;

    @Schema(title = "锁单人")
    private String locker;

    @Schema(title = "锁单状态")
    private String lockStatus;

    @Schema(title = "锁单人是否当前登录人 0否 1是")
    private Integer isLocker;

    @Schema(title = "审核状态")
    private String status;

    @Schema(title = "审核状态名称")
    private String statusText;
}
