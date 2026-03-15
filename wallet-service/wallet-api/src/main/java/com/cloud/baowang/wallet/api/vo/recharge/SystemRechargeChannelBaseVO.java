package com.cloud.baowang.wallet.api.vo.recharge;

import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "充值渠道信息")
public class SystemRechargeChannelBaseVO extends BaseVO {


    @Schema(description = "充值通道编号")
    private String rechargeChannelNo;
    /**
     * 货币代码
     */
    @Schema(description = "货币代码")
    private String currencyCode;

    /**
     * 通道类型
     */
    @Schema(description = "通道类型")
    private String channelType;

    @Schema(description = "充值方式Id")
    private String rechargeWayId;

    @Schema(description = "充值方式多语言")
    @I18nField
    private String rechargeWayI18;

    /**
     * 通道代码
     */
    @Schema(description = "通道代码")
    private String channelCode;

    /**
     * 通道名称
     * {@link com.cloud.baowang.common.core.enums.pay.PayChannelNameEnum}
     */
    @Schema(description = "通道名称")
    private String channelName;

    /**
     * 商户号
     */
    @Schema(description = "商户号")
    private String merNo;
    /**
     * 公钥
     */
    @Schema(description = "公钥")
    private String pubKey;
    /**
     * 私钥
     */
    @Schema(description = "私钥")
    private String privateKey;

    /**
     * 密钥
     */
    private String secretKey;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sortOrder;


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

    /**
     * 使用范围
     */
    @Schema(description = "使用范围")
    private String useScope;

    /**
     * 同类型权重
     */
    @Schema(description = "同类型权重")
    private Integer weight;

    /**
     * 授权数量
     */
    @Schema(description = "授权数量")
    private Integer authNum;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String memo;

    /**
     * 状态 0:禁用 1:启用
     */
    @Schema(description = "状态 0:禁用 1:启用")
    private Integer status;

    @Schema(description = "回调url")
    private String callbackUrl;

    @Schema(description = "支付api域名")
    private String apiUrl;

    @Schema(description = "姓名/电子钱包姓名")
    private String recvUserName;

    @Schema(description = "银行编码")
    private String recvBankCode;

    @Schema(description = "银行名称")
    private String recvBankName;
    @Schema(description = "开户行")
    private String recvBankBranch;
    @Schema(description = "银行帐号/电子钱包地址/虚拟币地址")
    private String recvBankCard;

    @Schema(description = "电子钱包账户")
    private String recvBankAccount;


    @Schema(description = "收款码")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String recvQrCode;

    @Schema(description = "收款码地址")
    private String recvQrCodeFileUrl;

    @Schema(description = "三方订单号")
    private String thirdOrderNo;

    /**
     * 扩展参数 JSON字符串
     */
    @Schema(description = "扩展参数 JSON字符串")
    private String extParam;
}
