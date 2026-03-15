package com.cloud.baowang.wallet.api.vo.recharge;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@I18nClass
@Schema(description = "充值列表返回")
public class RechargeWayListVO {


    @Schema(description = "充值类型Code bank_card银行卡 electronic_wallet电子钱包 crypto_currency加密货币 ")
    private String rechargeTypeCode;


    @Schema(description = "充值方式ID")
    private String id;



    /**
     * 充值方式
     */
    @Schema(description = "充值方式")
    @I18nField
    private String rechargeWay;



    /**
     * 图标
     */
    @Schema(description = "图标")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String wayIcon;

    @Schema(description = "图标完整路径")
    private String wayIconFileUrl;


    /**
     * 手续费 5 代表5%
     */
    @Schema(description = "手续费 5 代表5%")
    private BigDecimal wayFee;

    @Schema(description = "快捷金额，逗号隔开")
    private String quickAmount;

    /**
     * 充值最小值
     */
    @Schema(description = "充值最小值")
    private BigDecimal rechargeMin;

    /**
     * 充值最大值
     */
    @Schema(description = "充值最大值")
    private BigDecimal rechargeMax;


   /* @Schema(description = "优惠百分比")
    private BigDecimal discountPercent;*/


    @Schema(description = "是否推荐 0:未推荐 1:推荐")
    private Integer recommendFlag;

    /**
     * 网络协议类型 TRC20 ERC20
     */
    @Schema(description = "网络协议类型 TRC20 ERC20")
    private String networkType;


    /**
     * 货币代码
     */
    @Schema(description = "币种")
    private String currencyCode;

}
