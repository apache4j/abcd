package com.cloud.baowang.wallet.api.vo.fundrecord;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: wade
 */
@Data
@Schema(title = "会员充值人工确认审核-列表 返回")
public class UserRechargeReviewResponseVO {

    @Schema(title = "id")
    private Long id;

    @Schema(title = "订单号")
    private String orderNo;

    @Schema(title = "会员账号")
    private String userAccount;
    //userRegister

    @Schema(title = "会员注册信息")
    private String userRegister;
    @Schema(title = "存款人姓名")
    private String userName;

    @Schema(title = "存款金额")
    private BigDecimal applyAmount;

    @Schema(title = "支付方式")
    private String paymentMethod;
    @Schema(title = "支付方式-Name")
    private String paymentMethodName;

    @Schema(title = "申请时间")
    private Long applyTime;

    @Schema(title = "审核状态")
    private String status;
    @Schema(title = "审核状态-Name")
    private String statusName;

    @Schema(title = "申请附件1")
    private String applyFile;
    @Schema(title = "申请附件2-多个")
    private String cashFlowFile;
    @Schema(title = "充值资料")
    private List<String> remarkFiles;

    @Schema(title = "锁单状态 0未锁 1已锁")
    private Integer lockStatus;

    @Schema(title = "审核员/锁单人")
    private String locker;

    @Schema(title = "锁单人是否当前登录人 0否 1是")
    private Integer isLocker;

    @Schema(title = "一审人")
    private String oneReviewer;
    @Schema(title = "一审人是否当前登录人 0否 1是")
    private Integer isOneReviewer;

    @Schema(title = "收款账号信息")
    private List<String> paymentAccountInformation;

    @Schema(title = "账户类型 (银行卡为银行名称，虚拟币为币种)")
    private String accountType;

    @Schema(title = "账户分支")
    private String accountBranch;

    @Schema(title = "用户提/存款地址")
    private String withdrawAddress;

    @Schema(title = "提/存款用户名")
    private String withdrawName;

    @Schema(title = "充值类型")
    private String depositWithdrawType;


    @Schema(title = "充值方式")
    private String depositWithdrawMethod;

    @Schema(title = "充值币种")
    private String currency;

    @Schema(title = "充值币种金额")
    private BigDecimal tradeCurrencyAmount;

    @Schema(title = "手续费率")
    private BigDecimal feeRate;

    @Schema(title = "汇率")
    private BigDecimal exchangeRate;

    @Schema(title = "汇率描述")
    private String exchangeRateDesc;






}
