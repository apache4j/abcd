package com.cloud.baowang.wallet.api.vo.uservirtualcurrency;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderCustomerStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 用户存款取款
 *
 * @author qiqi
 */
@Data
@Accessors(chain = true)
@Schema(title = "取款信息返回前端VO")
@AllArgsConstructor
@NoArgsConstructor
@I18nClass
public class UserDepositWithdrawalResponseVO {


    /**
     * 会员账号
     */
    @Schema(title = "主键id")
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
    @Schema(title = "订单编号")
    private String orderNo;

    /**
     * 取款类型CODE
     */
    @Schema(title = "取款类型CODE")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.RECHARGE_TYPE)
    private String depositWithdrawTypeCode;

    /**
     * 取款类型CODE
     */
    @Schema(title = "取款类型CODE")
    private String depositWithdrawTypeCodeText;


    /**
     * 存取款方式
     */
    @I18nField
    @Schema(title = "存取款方式")
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
    @Schema(title = "存取款通道类型")
    private String depositWithdrawChannelType;


    /**
     * 币种
     */
    @Schema(title = "币种")
    private String currencyCode;

    /**
     * 汇率
     */
    @Schema(title = "汇率")
    private BigDecimal exchangeRate;

    /**
     * 交易币种金额
     */
    @Schema(title = "交易币种金额")
    private BigDecimal tradeCurrencyAmount;

    /**
     * 申请金额
     */
    @Schema(title = "申请金额")
    private BigDecimal applyAmount;

    /**
     * 实际到账金额
     */
    @Schema(title = "实际到账金额")
    private BigDecimal arriveAmount;

    /**
     * 手续费率
     */
    @Schema(title = "手续费率")
    private BigDecimal feeRate;

    /**
     * 手续费
     */
    @Schema(title = "手续费")
    private BigDecimal feeAmount;


    /**
     * 账户类型（ 银行卡为银行名称/code，虚拟币为币种 如USDT）
     */
    @Schema(title = "账户类型（ 银行卡为银行名称/code，虚拟币为币种 如USDT）")
    private String accountType;

    /**
     * 账户分支（银行卡为开户行/银行编码，虚拟币为链协议 如ERC20 TRC20)
     */
    @Schema(title = "账户分支（银行卡为开户行/银行编码，虚拟币为链协议 如ERC20 TRC20)")
    private String accountBranch;

    /**
     * 存取款地址 (银行账号 ，电子钱包账号，虚拟币地址）
     */
    @Schema(title = "存取款地址 (银行账号 ，电子钱包账号，虚拟币地址）")
    private String depositWithdrawAddress;

    /**
     * 存取款名字
     */
    @Schema(title = "存取款名字")
    private String depositWithdrawName;

    /**
     * 存取款姓
     */
    @Schema(title = "存取款姓")
    private String depositWithdrawSurname;

    /**
     * 手机区号
     */
    @Schema(title = "手机区号")
    private String areaCode;

    /**
     * 手机号
     */
    @Schema(title = "手机号")
    private String telephone;

    /**
     * 邮箱
     */
    @Schema(title = "邮箱")
    private String email;

    /**
     * cpf
     */
    @Schema(title = "CPF")
    private String cpf;

    /**
     * 地址
     */
    @Schema(title = "地址")
    private String address;

    /**
     * 省
     */
    @Schema(title = "省")
    private String province;

    /**
     * 城市
     */
    @Schema(title = "城市")
    private String city;

    /**
     * 邮政编码
     */
    @Schema(title = "邮政编码")
    private String postalCode;

    /**
     * 国家
     */
    @Schema(title = "国家")
    private String country;

    /**
     * 是否首次提款
     */
    @Schema(title = "是否首次提款")
    private String isFirstOut;

    /**
     * 是否连续出款
     */
    @Schema(title = "是否连续出款")
    private String isContinue;

    /**
     * 是否大额出款
     */
    @Schema(title = "是否大额出款")
    private String isBigMoney;

    /**
     * {@link DepositWithdrawalOrderStatusEnum}
     * 状态 1待一审 2一审审核 3一审拒绝 4待二审 5二审审核 6二审拒绝 7待出款 90 已关闭 96出款失败 97出款取消 98取消订单（申请人） 100失败 101成功
     */
    @Schema(title = "订单状态")
    private String status;

    /**
     * {@link com.cloud.baowang.common.core.enums.UserWithDrawReviewOperationEnum}
     * 审核操作
     */
    @Schema(title = "审核操作")
    private Integer reviewOperation;

    /**
     * {@link DepositWithdrawalOrderCustomerStatusEnum}
     * 客户端状态
     */
    @Schema(title = "客户端状态")
    private String customerStatus;

    /**
     * 出款审核人
     */
    @Schema(title = "出款审核人")
    private String payAuditUser;

    /**
     * 出款锁单时间
     */
    @Schema(title = "出款锁单时间")
    private Long payLockTime;

    /**
     * 出款时间
     */
    @Schema(title = "出款时间")
    private Long payAuditTime;

    /**
     * 出款备注
     */
    @Schema(title = "出款备注")
    private String payAuditRemark;

    /**
     * 存取款三方支付url
     */
    @Schema(title = "存取款三方支付URL")
    private String payThirdUrl;

    /**
     * 三方消息状态(1:获取中,2:已超时,3:异常,4:成功)
     */
    @Schema(title = "三方消息状态")
    private String payProcessStatus;

    /**
     * 三方支付失败原因
     */
    @Schema(title = "三方支付失败原因")
    private String payFailReason;

    /**
     * 三方关联流水ID
     */
    @Schema(title = "三方关联流水ID")
    private String payTxId;

    /**
     * 通道编码
     */
    @Schema(title = "通道编码")
    private String channelCode;

    /**
     * 申请IP
     */
    @Schema(title = "申请IP")
    private String applyIp;

    /**
     * 申请域名
     */
    @Schema(title = "申请域名")
    private String applyDomain;

    /**
     * 进出款凭证附件
     */
    @Schema(title = "进出款凭证附件")
    private String fileKey;

    /**
     * 资金流水凭证图片
     */
    @Schema(title = "资金流水凭证图片")
    private String cashFlowFile;

    /**
     * 资金流水备注
     */
    @Schema(title = "资金流水备注")
    private String cashFlowRemark;

    /**
     * 设备类型 来源
     */
    @Schema(title = "设备类型")
    private String deviceType;

    /**
     * 终端设备号
     */
    @Schema(title = "终端设备号")
    private String deviceNo;

    /**
     * 锁单状态
     */
    @Schema(title = "锁单状态")
    private Integer lockStatus;

    /**
     * 锁单人
     */
    @Schema(title = "锁单人")
    private String locker;

    /**
     * 锁单时间
     */
    @Schema(title = "锁单时间")
    private Long lockTime;

    /**
     * 申请备注
     */
    @Schema(title = "申请备注")
    private String applyRemark;

    /**
     * 备注
     */
    @Schema(title = "备注")
    private String remark;

    /**
     * 代理ID
     */
    @Schema(title = "代理ID")
    private String agentId;

    /**
     * 代理编码
     */
    @Schema(title = "代理编码")
    private String agentAccount;

    /**
     * 活动ID
     */
    @Schema(title = "活动ID")
    private Long activityBaseId;

    /**
     * 优惠百分比
     */
    @Schema(title = "优惠百分比")
    private BigDecimal discountPercent;

    /**
     * 催单标志(0未催单 1已催单)
     */
    @Schema(title = "催单标志")
    private Integer urgeOrder;

    /**
     * 提款搜集信息
     */
    @Schema(title = "提款搜集信息")
    private String collectInfo;


    @Schema(title = "风控层级")
    private String riskControlLevel;
    @Schema(title = "创建时间，申请时间")
    private Long createdTime;

    @Schema(title = "是否脱敏")
    private Boolean dataDesensitization;

}

