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
public class AccountUserCoinRequestMqVO extends MessageBaseVO {

    /**
     * 会员ID
     */
    @Schema( description ="会员ID")
    private String userId;

    /**
     * 上级代理
     */
    @Schema( description ="上级代理id")
    private String agentId;
    /**
     * 上级代理信息
     */
    @Schema( description ="上级代理账号")
    private String agentAccount;

    @Schema( description ="站点编码")
    private String siteCode;

    @Schema( description ="会员名称")
    private String userName;

    @Schema( description ="会员账号")
    private String userAccount;

    @Schema( description ="会员标签")
    private String userLabelId;

    @Schema( description ="VIP等级CODE")
    private Integer vipGradeCode;

    @Schema( description ="VIP等级")
    private Integer vipRank;

    @Schema( description ="账号状态")
    private String accountStatus;

    @Schema( description ="账号类型")
    private String accountType;

    @Schema( description ="风控层级ID")
    private String riskLevelId;


    @Schema( description ="风控层级")
    private String riskLevel;


    /**
     * 法币币种 必填
     */
    @Schema( description ="法币币种")
    private String currencyCode;
    /**
     * 系统订单号
     */
    @Schema( description ="系统订单号")
    private String innerOrderNo;
    /**
     * 三方关联订单号
     */
    @Schema( description ="三方交易单号-每一次交易唯一id")
    private String thirdOrderNo;

    /**
     * 三方CODE user，agent站点code,三方场馆venue_code/三方支付通道code
     */
    @Schema( description ="三方CODE user，agent站点code,三方场馆venue_code/三方支付通道code")
    private String toThirdCode;

    /**
     * 业务类型 必填
     * 对应枚举类  {@link AccountUserCoinEnum.BusinessCoinTypeEnum}
     */
    @Schema( description ="业务类型")
    private String businessCoinType;

    /**
     * 账变类型 必填
     * 对应枚举类  {@link AccountUserCoinEnum.CoinTypeEnum}
     */
    @Schema( description ="账变类型")
    private String coinType;


    /**
     * 客户端账变类型 {@link AccountUserCoinEnum.CustomerCoinTypeEnum}
     * 非必填
     */
    @Schema( description ="客户端账变类型 ")
    private String customerCoinType;

    /**
     * 收支类型 1收入 2支出 3冻结 4解冻
     * 对应枚举类 {@link com.cloud.baowang.account.api.enums.AccountBalanceTypeEnum}
     * 必填
     */
    @Schema( description ="收支类型 1收入 2支出 3冻结 4解冻")
    private String balanceType;

    /**
     * 金额改变数量 必填 所有游戏对接，账变记录 只做4位截断
     */
    @Schema( description ="金额改变数量 必填 所有游戏对接，账变记录 只做4位截断")
    private BigDecimal coinValue;

    /**
     * 冻结标记，为1 只扣除冻结金额
     *
     */
    @Schema( description ="冻结标记，为1 只扣除冻结金额")
    private Integer freezeFlag;

    /**
     * 账变时间
     */
    @Schema( description ="账变时间")
    private Long coinTime;

    /**
     * 活动标识
     * 对应枚举类 {@link com.cloud.baowang.account.api.enums.activity.AccountActivityTemplateEnum}
     * 必填
     */
    @Schema( description ="活动标识")
    private String activityFlag;

    /**
     * 备注
     */
    @Schema( description ="备注")
    private String remark;


    /**
     * 订单描述
     */
    @Schema( description ="订单描述")
    private String descInfo;

    /**
     * 账变计数,默认为1，重结算需往后+1
     */
    @Schema( description ="账变计数，不填",hidden = true)
    private Integer coinNum;

    /**
     * WTC汇率
     */
    @Schema( description ="WTC汇率")
    private BigDecimal finalRate;

    /**
     * 新的财务游戏必填-只要关注游戏的即可
     * 对应枚举类  {@link com.cloud.baowang.account.api.enums.AccountCoinTypeEnums}
     */
    @Schema( description ="业务类型")
    private String accountCoinType;

}
