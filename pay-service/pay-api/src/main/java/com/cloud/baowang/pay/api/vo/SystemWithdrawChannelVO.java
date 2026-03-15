package com.cloud.baowang.pay.api.vo;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

import static com.cloud.baowang.common.core.constants.CommonConstant.CHANNEL_TYPE;
import static com.cloud.baowang.common.core.constants.I18nFieldTypeConstants.DICT;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/27 11:38
 * @Version: V1.0
 **/
@Data
@Schema(description = "提现渠道")
@I18nClass
public class SystemWithdrawChannelVO {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private String id;


    /**
     * 提现通道编号
     */
    @Schema(description = "提现通道编号")
    private String withdrawChannelNo;

    /**
     * 货币代码
     */
    @Schema(description = "货币代码")
    private String currencyCode;

    /**
     * 通道类型
     */
    @Schema(description = "通道类型")
    @I18nField(type = DICT,value = CHANNEL_TYPE)
    private String channelType;

    @Schema(description = "通道类型名称")
    private String channelTypeText;


    @Schema(description = "提款方式Id")
    private String withdrawWayId;

    @Schema(description = "提款类型Id")
    private String withdrawTypeId;


    @Schema(description = "提款类型编码")
    private String withdrawTypeCode;

    /**
     * 提现方式
     */
    @Schema(description = "提现方式 多语言")
    @I18nField
    private String withdrawWayI18;

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
     * 密钥
     */
    @Schema(description = "密钥")
    private String privateKey;


    @Schema(description = "密钥")
    private String secretKey;

    /**
     * 提现最小值
     */
    @Schema(description = "提现最小值")
    private BigDecimal withdrawMin;

    /**
     * 提现最大值
     */
    @Schema(description = "提现最大值")
    private BigDecimal withdrawMax;


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
    @I18nField(type = DICT, value = CommonConstant.ENABLE_DISABLE_STATUS)
    private Integer status;

    @Schema(description = "状态名称 0:禁用 1:启用")
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
     * 扩展参数 JSON字符串
     */
    @Schema(description = "扩展参数 JSON字符串")
    private String extParam;
}
