package com.cloud.baowang.wallet.api.vo.recharge;

import com.cloud.baowang.common.core.vo.base.MessageBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Schema(description = "充值触发类")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RechargeTriggerVO extends MessageBaseVO implements Serializable {


    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * {@link com.cloud.baowang.activity.api.enums.DepositTypeEnum }
     */
    @Schema(description = "充值次数类型")
    private Integer depositType;

    @Schema(description = "会员id")
    private String userId;

    @Schema(description = "会员账号")
    private String userAccount;

    /**
     * 非必填字段
     * 首存、次存活动不关注订单号
     * 其他活动需要必填此字段
     */
    @Schema(description = "充值单号")
    private String orderNumber;

    @Schema(description = "充值时间")
    private Long rechargeTime;

    @Schema(description = "充值金额")
    private BigDecimal rechargeAmount;

    @Schema(description = "币种代码")
    private String currencyCode;

    /**
    * 是否手动申请 true:手动申请 false: 开始派发
    * */
    private boolean applyFlag=false;

    //非必填
    @Schema(description = "当前充值金额+币种代码换算后对应的平台币数量")
    private BigDecimal platformCurrency;
    //非必填
    @Schema(description = "累计充值金额")
    private BigDecimal totalRecharge;

    @Schema(description = "时区")
    private String timezone;

    @Schema(description = "注册时间")
    private Long registerTime;
}
