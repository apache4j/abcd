package com.cloud.baowang.wallet.api.vo.recharge;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import static com.cloud.baowang.common.core.constants.I18nFieldTypeConstants.DICT;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/27 11:38
 * @Version: V1.0
 **/
@Data
@Schema(description = "客户端充值渠道响应")
@I18nClass
public class SiteSystemRechargeChannelRespVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private String id;



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
    @I18nField(type = DICT,value = CommonConstant.CHANNEL_TYPE)
    private String channelType;

    @Schema(description = "通道类型名称")
    private String channelTypeText;

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

    @Schema(description = "密钥")
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
     * 通道手续费
     */
    @Schema(description = "通道手续费")
    private BigDecimal fee;

    /**
     * 使用范围
     */
    @Schema(description = "使用范围")
    private String useScope;


    @Schema(description = "使用范围描述")
    private String useScopeText;

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
    @I18nField(type = DICT,value = CommonConstant.ENABLE_DISABLE_TYPE)
    private Integer status;

    private String statusText;

    @Schema(description = "回调url")
    private String callbackUrl;

    @Schema(description = "支付api域名")
    private String apiUrl;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String creator;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Long createdTime;

    /**
     * 修改人
     */
    @Schema(description = "修改人")
    private String updater;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    private Long updatedTime;

    /**
     * 姓名/电子钱包姓名
     */
    private String recvUserName;

    /**
     * 银行编码
     */
    private String recvBankCode;
    /**
     * 银行名称
     */
    private String recvBankName;
    /**
     * 开户行
     */
    private String recvBankBranch;
    /**
     * 银行帐号/电子钱包地址/虚拟币地址
     */
    private String recvBankCard;
    /**
     * 电子钱包账户
     */
    private String recvBankAccount;

    /**
     * 收款码
     */
    private String recvQrCode;

    /**
     * vip等级使用范围
     */
    @Schema(description = "vip等级使用范围")
    private String vipRankUseScope;
    /**
     * vip等级使用范围
     */
    @Schema(description = "vip等级使用范围")
    private String vipGradeUseScope;

}
