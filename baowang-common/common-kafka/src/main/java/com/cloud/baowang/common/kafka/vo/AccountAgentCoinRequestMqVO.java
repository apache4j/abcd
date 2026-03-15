package com.cloud.baowang.common.kafka.vo;

import com.cloud.baowang.common.core.vo.base.MessageBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * @author: mufan
 * @createTime: 2025/10/25 18:11
 * @description:
 */

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "注单第一次结算发送的消息实体")
public class AccountAgentCoinRequestMqVO extends MessageBaseVO {

    @Schema( description ="站点编码")
    private String siteCode;

    @Schema( description ="代理id")
    private String agentId;


    /**
     * 代理账号
     */
    @Schema( description ="代理账号")
    private String agentAccount;

    /**
     * 代理名称
     */
    @Schema( description ="代理名称")
    private String agentName;

    /**
     * 父节点
     */
    @Schema( description ="父节点 上级代理id")
    private String parentId;

    /**
     * 层次id 逗号分隔
     */
    @Schema( description ="层次id 逗号分隔")
    private String path;

    /**
     * 代理层级
     */
    @Schema( description ="代理层级")
    private Integer level;

    /**
     * 代理账号状态 1正常 2登录锁定 3充提锁定(状态多选,用逗号分开)
     */
    @Schema( description ="代理账号状态 1正常 2登录锁定 3充提锁定(状态多选,用逗号分开)")
    private String status;

    /**
     * 代理标签id
     */
    @Schema( description ="代理标签id")
    private String agentLabelId;

    /**
     * 风控层级id
     */
    @Schema( description ="风控层级id")
    private String riskLevelId;


    /**
     * 币种
     */
    @Schema( description ="币种 WTC")
    private String currency;

    /**
     * 内部订单号
     */
    @Schema( description ="系统订单")
    private String innerOrderNo;
    /**
     * 三方关联订单号
     */
    @Schema( description ="三方关联订单号")
    private String thirdOrderNo;

    /**
     * 三方CODE user，agent站点code,三方场馆venue_code，三方支付code
     */
    @Schema( description ="三方CODE user，agent站点code,三方场馆venue_code，三方支付code")
    private String toThirdCode;
    /**
     * 代理钱包类型
     * 对应枚举类  {@link com.cloud.baowang.account.api.enums.AccountAgentCoinRecordTypeEnum.AgentWalletTypeEnum}
     */
    @Schema( description ="代理钱包类型")
    private String agentWalletType;

    /**
     * 业务类型
     * 对应枚举类 {@link com.cloud.baowang.account.api.enums.AccountAgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum}
     *
     */
    @Schema( description ="业务类型")
    private String businessCoinType;

    /**
     * 账变类型
     * 对应枚举 {@link com.cloud.baowang.account.api.enums.AccountAgentCoinRecordTypeEnum.AgentCoinTypeEnum}
     */
    @Schema( description ="账变类型")
    private String coinType;

    /**
     * 客户端账变类型
     * 对应枚举 {@link com.cloud.baowang.account.api.enums.AccountAgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum}
     */
    @Schema( description ="客户端账变类型")
    private String customerCoinType;

    /**
     * 收支类型 1收入 2支出 3冻结 4解冻
     * 对应枚举类 {@link com.cloud.baowang.account.api.enums.AccountAgentCoinRecordTypeEnum.AgentBalanceTypeEnum}
     */
    @Schema( description ="收支类型 1收入 2支出 3冻结 4解冻")
    private String balanceType;

    /**
     * 金额改变数量
     */
    @Schema( description ="金额改变数量")
    private BigDecimal coinValue;


    /**
     * 备注
     */
    @Schema( description ="备注")
    private String remark;

    /**
     * 支出类型 0 余额支付 1冻结支出
     */
    @Schema( description ="支出类型 0 余额支付 1冻结支出")
    private Integer freezeFlag;


    /**
     * 账变时间
     */
    @Schema( description ="账变时间")
    private Long coinTime;

    /**
     * WTC汇率
     */
    @Schema( description ="WTC汇率")
    private BigDecimal finalRate;

}
