package com.cloud.baowang.wallet.api.vo.recharge;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Schema(title ="客户端充值类型返回对象VO")
@I18nClass
public class RechargeTypeListVO {
//
//    /**
//     * 充值类型
//     */
//    @Schema(description = "充值类型")
//    private String rechargeType;

    @Schema(description = "充值类型Id")
    private String rechargeTypeId;

    @Schema(description = "充值类型Code bank_card银行卡 electronic_wallet电子钱包 crypto_currency加密货币 bank_card_transfer银行转账 ")
    private String rechargeTypeCode;

    /**
     * 充值类型 多语言
     */

    @Schema(description = "充值类型 多语言代码")
    @I18nField
    private String rechargeType;



    @Schema(description = "充值方式列表")
    private List<RechargeWayListVO> rechargeWayListVOS;

}
