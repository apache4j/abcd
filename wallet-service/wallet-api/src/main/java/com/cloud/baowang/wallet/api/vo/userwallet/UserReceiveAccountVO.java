package com.cloud.baowang.wallet.api.vo.userwallet;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "大陆盘-会员收款详细信息")
@I18nClass
public class UserReceiveAccountVO {


    /**
     * id
     */
    @Schema(description = "id")
    private String id;


    /**
     * 会员账号
     */
    private String userAccount;


    /**
     * 收款类型  bank_card银行卡 electronic_wallet电子钱包 crypto_currency加密货币
     */
    @Schema(description = "收款类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.WITHDRAW_TYPE )
    private String receiveType;

    @Schema(description = "收款类型名称")
    private String receiveTypeText;


    /**
     * 银行卡/电子钱包姓名
     */
    @Schema(description = "银行卡/电子钱包姓名")
    private String surname;



    /**
     * 银行名称
     */
    @Schema(description = "银行名称")
    @I18nField
    private String bankName;


    /**
     * 银行账号
     */
    @Schema(description = "银行代码")
    private String bankCard;


    /**
     * 电子钱包账户
     */
    @Schema(description = "电子钱包账户")
    private String electronicWalletAccount;

    /**
     * 电子钱包名称
     */
    @Schema(description = "电子钱包名称")
    private String electronicWalletName;

    /**
     * 链网络类型
     */
    @Schema(description = "链网络类型")
    private String networkType;

    /**
     * 加密货币收款地址
     */
    @Schema(description = "加密货币收款地址")
    private String addressNo;


    /**
     * 风控层级id
     */
    private String riskControlLevelId;

    /**
     * 风控层级
     */
    @Schema(description = "风控层级")
    private String riskControlLevel;

    /**
     * 是否默认 0否 1是
     */
    @Schema(title = "是否默认 0否 1是")
    private String defaultFlag;

    /**
     * 备注
     */
    @Schema(title = "备注")
    private String remark;

    @Schema(title = "绑定时间")
    private Long createdTime;


    /**
     * 是否解绑审核中
     */
    @Schema(title = "是否解绑审核中 0否 1是")
    private String unBindFlag;


}
