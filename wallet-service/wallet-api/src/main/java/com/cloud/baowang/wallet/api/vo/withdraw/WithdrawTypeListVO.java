package com.cloud.baowang.wallet.api.vo.withdraw;


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
@Schema(title ="客户端提现类型返回对象VO")
@I18nClass
public class WithdrawTypeListVO {

  /*  *//**
     * 提现类型
     *//*

    @Schema(description = "提现类型")
    private String withdrawType;*/

    /**
     * 提现类型id
     */
    @Schema(description = "提现类型ID")
    private String withdrawTypeId;

    /**
     * 提现类型CODE
     */
    @Schema(description = "提现类型CODE bank_card银行卡 electronic_wallet电子钱包 crypto_currency加密货币 bank_card_transfer银行转账 ")
    private String withdrawTypeCode;

    /**
     * 提现类型 多语言
     */

    @Schema(description = "提现类型 多语言代码")
    @I18nField
    private String withdrawType;




    @Schema(description = "提现方式列表")
    private List<WithdrawWayListVO> withdrawWayListVOS;

}
