package com.cloud.baowang.wallet.po;


import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderCustomerStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;

/**
 * 用户存款取款
 *
 * @author qiqi
 */
@Data
@Accessors(chain = true)
@FieldNameConstants
@TableName("user_deposit_withdrawal")
public class UserDepositWithdrawalPO extends BasePO {


    /**
     * 会员账号
     */
    private String userId;


    /**
     * 会员账号
     */
    private String userAccount;

    /**
     * 站点CODE
     */
    private String siteCode;


    /**
     * 订单类型 1 存款 2 取款
     * {@link DepositWithdrawalOrderTypeEnum}
     */
    private Integer type;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 存取款类型CODE
     */
    private String depositWithdrawTypeCode;
    /**
     * 存取款类型ID
     */
    private String depositWithdrawTypeId;

    /**
     * 存取款方式id
     */
    private String depositWithdrawWayId;

    /**
     * 存取款方式
     */
    private String depositWithdrawWay;

    /**
     * 存取款通道id
     */
    private String depositWithdrawChannelId;


    /**
     * 存取款通道CODE
     */
    private String depositWithdrawChannelCode;

    /**
     * 存取款通道Name
     */
    private String depositWithdrawChannelName;

    /**
     * 存取款通道类型(THIRD 三方，OFFLINE 线下）
     */
    private String depositWithdrawChannelType;


    /**
     * 币种
     */
    private String currencyCode;

    /**
     * 汇率
     */
    private BigDecimal exchangeRate;

    /**
     * 主货币-USD汇率（总控)
     */
    private BigDecimal currencyUsdExchangeRate;


    /**
     * 交易币种金额
     */
    private BigDecimal tradeCurrencyAmount;

    /**
     * 申请金额
     */
    private BigDecimal applyAmount;

    /**
     * 实际到账金额
     */
    private BigDecimal arriveAmount;


    /**
     * 会员手续费类型 0百分比 1固定金额
     */
    private Integer feeType;

    /**
     * 结算手续费类型 0百分比 1固定金额 2百分比+固定金额
     */
    private Integer wayFeeType;

    /**
     * 百分比金额
     */
    private BigDecimal settlementFeePercentageAmount;

    /**
     * 固定金额
     */
    private BigDecimal settlementFeeFixedAmount;

    /**
     * 手续费率
     */
    private BigDecimal feeRate;

    /**
     * 会员百分比手续费
     */
    private BigDecimal feeAmount;

    /**
     * 会员固定金额手续费
     */
    private BigDecimal feeFixedAmount;


    /**
     * 结算手续费率
     */
    private BigDecimal settlementFeeRate;

    /**
     * 方式手续费
     */
    private BigDecimal wayFeeAmount;

    /**
     * 结算手续费
     */
    private BigDecimal settlementFeeAmount;


    /**
     * 账户类型（ 银行卡为银行名称，虚拟币为币种 如USDT,）
     */
    private String accountType;

    /**
     * 账户分支（银行卡为开户行/银行编码，虚拟币为链协议 如ERC20 TRC20,电子钱包)
     */
    private String accountBranch;

    /**
     * 存取款地址 (银行账号 ，电子钱包账号，虚拟币地址）
     */
    private String depositWithdrawAddress;

    /**
     * 存取款名字
     */
    private String depositWithdrawName;


    /**
     * 存取款姓
     */
    private String depositWithdrawSurname;


    /**
     * 手机区号
     */
    private String areaCode;


    /**
     * 手机号
     */
    private String telephone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * cpf
     */
    private String cpf;

    /**
     * 地址
     */
    private String address;

    /**
     * 省
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 邮政编码
     */
    private String postalCode;

    /**
     * 国家
     */
    private String country;

    /**
     * IFSC码(印度)
     */
    private String ifscCode;


    /**
     * 电子钱包名称
     */
    private String electronicWalletName;

    /**
     * 是否首次提款
     */
    private String isFirstOut;

    /**
     * 是否连续出款
     */
    private String isContinue;

    /**
     * 是否大额出款
     */
    private String isBigMoney;

    /**
     * {@link DepositWithdrawalOrderStatusEnum}
     * 状态 1待一审 2一审审核 3一审拒绝 4待二审 5二审审核 6二审拒绝 7待出款 90 已关闭 96出款失败 97出款取消 98取消订单（申请人） 100失败 101成功
     */
    private String status;
    /**
     * {@link com.cloud.baowang.common.core.enums.UserWithDrawReviewOperationEnum}
     * 审核操作
     */
    private Integer reviewOperation;

    /**
     * {@link DepositWithdrawalOrderCustomerStatusEnum}
     * 客户端状态
     */
    private String customerStatus;

    /**
     * 出款审核人
     */
    private String payAuditUser;

    /**
     * 出款锁单时间
     */
    private Long payLockTime;

    /**
     * 出款时间
     */
    private Long payAuditTime;

    /**
     * 出款备注
     */
    private String payAuditRemark;

    /**
     * 存取款三方支付url
     */
    private String payThirdUrl;

    /**
     * 三方消息状态(1:获取中,2:已超时,3:异常,4:成功)
     */
    private String payProcessStatus;

    /**
     * 三方支付失败原因
     */
    private String payFailReason;

    /**
     * 三方关联流水id
     */
    private String payTxId;

    /**
     * 通道编码
     */
    private String channelCode;


    /**
     * 申请IP
     */
    private String applyIp;

    /**
     * 申请域名
     */
    private String applyDomain;


    /**
     * 进出款凭证附件
     */
    private String fileKey;

    /**
     * 资金流水凭证图片
     */
    private String cashFlowFile;

    /**
     * 资金流水备注
     */
    private String cashFlowRemark;

    /**
     * 设备类型 来源
     */
    private String deviceType;

    /**
     * 终端设备号
     */
    private String deviceNo;

    /**
     * 锁单状态
     */
    private Integer lockStatus;

    /**
     * 锁单人
     */
    private String locker;


    /**
     * 锁单时间
     */
    private Long lockTime;


    /**
     * 申请备注
     */
    private String applyRemark;


    /**
     * 备注
     */
    private String remark;

    /**
     * 代理ID
     */
    private String agentId;

    /**
     * 代理编码
     */
    private String agentAccount;


    /**
     * 活动ID
     */
    private Long activityBaseId;

    /**
     * 优惠百分比
     */
    private BigDecimal discountPercent;

    /**
     * 催单标志(0未催单 1已催单)
     */
    private Integer urgeOrder;

    /**
     * 提款搜集信息
     */
    private String collectInfo;

    /**
     * 合并充值标记（虚拟币小额多笔）1 是 0否
     */
    private Integer combinedRecharge;

    /**
     * 通道存款/提款耗时 毫秒
     */
    private Long rechargeWithdrawTimeConsuming;

    /**
     * 交易币种
     */
    private String coinCode;
    /**
     * 待出款方式类型
     */
    private String payoutType;

    /**
     * 会员标签ID
     */
    private String userLabelId;


    /**
     * 收款 姓名/电子钱包姓名
     */
    private String recvUserName;

    /**
     * 收款 银行编码
     */
    private String recvBankCode;
    /**
     * 收款银行名称
     */
    private String recvBankName;
    /**
     * 收款开户行
     */
    private String recvBankBranch;

    /**
     * 收款电子钱包账户
     */
    private String recvBankAccount;

    /**
     * 收款码
     */
    private String recvQrCode;

}
