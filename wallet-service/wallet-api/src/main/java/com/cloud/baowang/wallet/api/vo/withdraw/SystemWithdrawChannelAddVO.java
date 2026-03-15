package com.cloud.baowang.wallet.api.vo.withdraw;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/27 11:38
 * @Version: V1.0
 **/
@Data
@Schema(description = "提现渠道")
public class SystemWithdrawChannelAddVO {

    @Schema(description = "操作人 ")
    private String operatorUserNo;
    /**
     * 货币代码
     */
    @Schema(description = "货币代码")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String currencyCode;


    @Schema(description = "通道类型")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String channelType;


    @Schema(description = "提现方式ID")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String withdrawWayId;


    @Schema(description = "通道代码")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String channelCode;


    @Schema(description = "通道名称 从common下拉框中获取 pay_channel_name")
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

    @Schema(description = "回调url")
    private String callbackUrl;

    @Schema(description = "支付api域名")
    private String apiUrl;


    /**
     * 提现最小值
     */
    @Schema(description = "提现最小值")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @DecimalMin(value = "0.01",message = ConstantsCode.PARAM_ERROR)
    private BigDecimal withdrawMin;

    /**
     * 提现最大值
     */
    @Schema(description = "提现最大值")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @DecimalMin(value = "0.01",message = ConstantsCode.PARAM_ERROR)
    private BigDecimal withdrawMax;

    /**
     * 使用范围
     */
    @Schema(description = "使用范围")
    private String useScope;

    /**
     * 通道手续费
     */
    @Schema(description = "通道手续费")
    private BigDecimal fee;



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
     * 扩展参数 JSON字符串
     */
    @Schema(description = "扩展参数 JSON字符串")
    private String extParam;

}
