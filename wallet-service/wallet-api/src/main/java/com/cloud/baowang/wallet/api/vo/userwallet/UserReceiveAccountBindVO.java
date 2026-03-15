package com.cloud.baowang.wallet.api.vo.userwallet;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(title = "大陆盘-会员收款信息解绑请求对象")
public class UserReceiveAccountBindVO {



    /**
     * 收款类型  bank_card银行卡 electronic_wallet电子钱包 crypto_currency加密货币
     */
    @Schema(description = "收款类型  bank_card银行卡 electronic_wallet电子钱包 crypto_currency加密货币")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String receiveType;

    /**
     * 银行卡/电子钱包姓名
     */
    @Schema(description = "银行卡/电子钱包姓名")
    private String surname;


    @Schema(description = "银行卡邮箱")
    private String userEmail;



    /**
     * 手机区号
     */
    @Schema(description = "手机区号")
    private String areaCode;
    /**
     * 银行卡/电子钱包手机号
     */
    @Schema(description = "银行卡/电子钱包手机号")
    private String userPhone;

    /**
     * 银行名称
     */
    @Schema(description = "银行名称")
    private String bankName;

    /**
     * 开户行
     */
    @Schema(description = "开户行")
    private String bankBranch;

    /**
     * 银行代码
     */
    @Schema(description = "银行代码")
    private String bankCode;

    /**
     * 银行账号
     */
    @Schema(description = "银行账号")
    private String bankCard;

    /**
     * IFSC
     */
    @Schema(description = "IFSC")
    private String ifscCode;


    /**
     * 省
     */
    @Schema(description = "省")
    private String provinceName;


    /**
     * 城市
     */
    @Schema(description = "城市")
    private String cityName;


    /**
     * 详细地址
     */
    @Schema(description = "详细地址")
    private String detailAddress;

    /**
     * 电子钱包账户
     */
    @Schema(description = "电子钱包账户")
    private String userAccount;

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
     * 电子钱包/加密货币收款地址
     */
    @Schema(description = "电子钱包/加密货币收款地址")
    private String addressNo;




    /**
     * 是否默认 0否 1是
     */
    @Schema(description = "是否默认 0否 1是")
    private String defaultFlag;

    /**
     * 备注
     */
    @Schema(description = "收款类型  bank_card银行卡 electronic_wallet电子钱包 crypto_currency加密货币")
    private String remark;


    @Schema(description = "交易密码")
    private String withdrawPassWord;


}
