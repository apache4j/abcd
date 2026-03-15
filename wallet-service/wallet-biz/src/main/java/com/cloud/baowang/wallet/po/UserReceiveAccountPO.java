package com.cloud.baowang.wallet.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("user_receive_account")
public class UserReceiveAccountPO extends BasePO {

    /**
     * 站点编码
     */
    private String siteCode;

    /**
     * 会员id
     */
    private String userId;

    /**
     * 会员账号
     */
    private String userAccount;


    /**
     * 收款类型  bank_card银行卡 electronic_wallet电子钱包 crypto_currency加密货币
     */
    private String receiveType;

    /**
     * 银行卡/电子钱包姓名
     */
    private String surname;

    /**
     * 银行卡邮箱
     */
    private String userEmail;


    /**
     * 手机区号
     */
    private String areaCode;
    /**
     * 银行卡/电子钱包手机号
     */
    private String userPhone;

    /**
     * 银行名称
     */
    private String bankName;

    /**
     * 开户行
     */
    private String bankBranch;

    /**
     * 银行代码
     */
    private String bankCode;

    /**
     * 银行账号
     */
    private String bankCard;

    /**
     * IFSC
     */
    private String ifscCode;


    /**
     * 省
     */
    private String provinceName;


    /**
     * 城市
     */
    private String cityName;


    /**
     * 详细地址
     */
    private String detailAddress;

    /**
     * 电子钱包账户
     */
    private String electronicWalletAccount;

    /**
     * 电子钱包名称
     */
    private String electronicWalletName;

    /**
     * 链网络类型
     */
    private String networkType;

    /**
     * 电子钱包/加密货币收款地址
     */
    private String addressNo;

    /**
     * 绑定状态 0未绑定 1绑定中
     */
    private String bindingStatus;

    /**
     * 风控层级id
     */
    private String riskControlLevelId;

    /**
     * 风控层级
     */
    private String riskControlLevel;

    /**
     * 是否默认 0否 1是
     */
    private String defaultFlag;

    /**
     * 备注
     */
    private String remark;


}
