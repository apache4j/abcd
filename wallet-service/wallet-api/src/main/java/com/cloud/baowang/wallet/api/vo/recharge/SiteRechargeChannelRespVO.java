package com.cloud.baowang.wallet.api.vo.recharge;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import static com.cloud.baowang.common.core.constants.I18nFieldTypeConstants.DICT;

/**
 * @Desciption:
 * @Author: qiqi
 **/
@Data
@Schema(description = "站点存款通道响应")
@I18nClass
public class SiteRechargeChannelRespVO implements Serializable {

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

    @Schema(description = "充值类型编码")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.RECHARGE_TYPE)
    private String rechargeTypeCode;

    @Schema(description = "充值类型名称")
    private String rechargeTypeCodeText;

    @Schema(description = "虚拟币网络类型 TRC20 ERC20")
    private String networkType;

    /**
     * 通道ID
     */
    @Schema(description = "通道ID")
    private String channelId;

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
     * 排序
     */
    @Schema(description = "排序")
    private Integer sortOrder;


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
     * VIP等级使用范围
     */
    @Schema(description = "VIP等级使用范围")
    private String vipGradeUseScope;

    @Schema(description = "VIP等级使用范围")
    private String vipGradeUseScopeText;
}
