package com.cloud.baowang.agent.api.vo.withdrawConfig;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author qiqi
 */
@Data
@Schema(title = "客户端 代理提款配置返回信息对象")
public class AgentWithdrawConfigResVO {

    @Schema(description = "银行卡单次提款最低额度")
    private BigDecimal bankCardWithdrawMinQuota;

    @Schema(description = "银行卡单次提款最高额度")
    private BigDecimal bankCardWithdrawMaxQuota;

    @Schema(description = "虚拟币单次提款最低额度")
    private BigDecimal virtualCurrencyWithdrawMinQuota;

    @Schema(description = "虚拟币单次提款最高额度")
    private BigDecimal virtualCurrencyWithdrawMaxQuota;

    @Schema(description = "单日提款总次数")
    private Integer dayWithdrawNum;

    @Schema(description = "单日提款总额度")
    private BigDecimal dayWithdrawTotalAmount;

    @Schema(description = "当日剩余免费额度")
    private BigDecimal dayRemindWithdrawQuota;

    @Schema(description = "当日剩余免费次数")
    private Integer dayRemindWithdrawNums;

    @Schema(description = "汇率")
    private BigDecimal exchangeRate;

    @Schema(description = "累计提款金额")
    private BigDecimal withdrawalAmount;

    @Schema(description = "累计订单")
    private Integer withdrawalNums;

    @Schema(description = "佣金钱包余额")
    private BigDecimal commissionCoinBalance;

    @Schema(description = "是否绑定支付密码 0否 1是 ")
    private Integer isBindPayPassword;

    @Schema(description = "是否绑定谷歌秘钥 0否 1是")
    private Integer isBindGoogleAuthKey;

}
