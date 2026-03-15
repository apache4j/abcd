package com.cloud.baowang.agent.po;


import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderCustomerStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;

/**
 * 会员人工出款记录
 *
 * @author qiqi
 */
@Data
@Accessors(chain = true)
@FieldNameConstants
@TableName("agent_withdrawal_manual_record")
public class AgentWithdrawalManualRecordPO extends BasePO {

    /**
     * 代理ID
     */
    private String agentId;

    /**
     * 代理编码
     */
    private String agentAccount;

    /**
     * 站点CODE
     */
    private String siteCode;


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
     * 币种
     */
    private String currencyCode;

    /**
     * 申请金额
     */
    private BigDecimal applyAmount;

    /**
     * 交易币种金额
     */
    private BigDecimal tradeCurrencyAmount;

    /**
     * 实际到账金额
     */
    private BigDecimal arriveAmount;


    /**
     * 会员手续费类型 0百分比 1固定金额
     */
    private Integer feeType;

    /**
     * 手续费率
     */
    private BigDecimal feeRate;

    /**
     * 手续费
     */
    private BigDecimal feeAmount;

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
     * ifsc
     */
    private String ifscCode;

    /**
     * 电子钱包名称
     */
    private String electronicWalletName;

    /**{@link DepositWithdrawalOrderCustomerStatusEnum}
     * 状态
     */
    private String customerStatus;




    /**
     * 出款凭证附件
     */
    private String fileKey;


    /**
     * 设备类型 来源
     */
    private String deviceType;

    /**
     * 终端设备号
     */
    private String deviceNo;



    /**
     * 备注
     */
    private String remark;



}
