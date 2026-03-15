package com.cloud.baowang.wallet.api.vo.userwallet;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: kimi
 */
@Data
@Schema(title = "会员虚拟币账号管理 返回")
public class WalletCurrencyManageResponseVO {

    @Schema(title = "id")
    private Long id;

    @Schema(title = "电子钱包账号地址")
    private String walletCurrencyAddress;

    @Schema(title = "电子钱包名称")
    private String walletCurrencyAlias;

    /*@Schema(title = "虚拟币协议")
    private String virtualCurrencyProtocol;

    @Schema(title = "黑名单状态 0禁用 1启用")
    private Integer blackStatus;
    @Schema(title = "黑名单状态 0禁用 1启用")
    private String blackStatusName;

    @Schema(title = "绑定状态 0未绑定 1绑定中")
    private Integer bindingStatus;
    @Schema(title = "绑定状态 0未绑定 1绑定中")
    private String bindingStatusName;

    @Schema(title = "风控层级id")
    private Long riskControlLevelId;
    @Schema(title = "风控层级")
    private String riskControlLevel;

    @Schema(title = "绑定账号数量")
    private Integer bindingAccountTimes;

    @Schema(title = "当前绑定会员账号")
    private String currentBindingUserAccount;

    @Schema(title = "会员姓名")
    private String userName;

    @Schema(title = "会员提款成功次数")
    private Integer userWithdrawSuccessTimes;

    @Schema(title = "会员提款被拒次数")
    private Integer userWithdrawFailTimes;

    @Schema(title = "会员提款总金额")
    private BigDecimal userWithdrawSumAmount;

    @Schema(title = "代理提款成功次数")
    private Integer agentWithdrawSuccessTimes;

    @Schema(title = "代理提款被拒次数")
    private Integer agentWithdrawFailTimes;

    @Schema(title = "代理提款总金额")
    private BigDecimal agentWithdrawSumAmount;

    @Schema(title = "电子钱包账号新增时间")
    private Long firstUseTime;

    @Schema(title = "最近提款时间")
    private Long lastWithdrawTime;

    @Schema(title = "最近操作人")
    private String lastOperator;

    @Schema(title = "最近操作时间")
    private Long updatedTime;*/
}
