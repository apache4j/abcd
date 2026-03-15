package com.cloud.baowang.wallet.api.vo.recharge;

import com.cloud.baowang.common.core.constants.CommonConstant;
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
@Schema(description = "充值渠道创建")
public class SystemRechargeChannelNewReqVO {

    @Schema(description = "操作人 ",hidden = true)
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

    @Schema(description = "充值方式Id")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String rechargeWayId;

    @Schema(description = "通道代码")
  //  @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String channelCode;


    @Schema(description = "通道名称 从common下拉框中获取 pay_channel_name")
  //  @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String channelName;

    /**
     * 商户号
     */
    @Schema(description = "商户号")
    //@NotNull(message = "商户号不能为空")
   // @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String merNo;
    /**
     * 公钥
     */
    @Schema(description = "公钥")
    //@NotNull(message = "公钥不能为空")
   // @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String pubKey;
    /**
     * 密钥
     */
    @Schema(description = "密钥")
   // @NotNull(message = "密钥不能为空")
    private String privateKey;

    /**
     * 排序
     */
    @Schema(description = "排序")
   // @NotNull(message = "排序不能为空")
    private Integer sortOrder;


    /**
     * 充值最小值
     */
    @Schema(description = "充值最小值")
    //@NotNull(message = "充值最小值不能为空")
   // @NotNull(message = ConstantsCode.PARAM_ERROR)
   // @DecimalMin(value = "0.01",message = ConstantsCode.PARAM_ERROR)
    private BigDecimal rechargeMin;

    /**
     * 充值最大值
     */
    @Schema(description = "充值最大值")
    //@NotNull(message = "充值最大值不能为空")
   // @NotNull(message = ConstantsCode.PARAM_ERROR)
   // @DecimalMin(value = "0.01",message = ConstantsCode.PARAM_ERROR)
    private BigDecimal rechargeMax;

    /**
     * 通道手续费
     */
    @Schema(description = "通道手续费")
    private BigDecimal fee;

    /**
     * 使用范围
     */
    @Schema(description = "使用范围 多个值中间逗号分隔")
    //@NotNull(message = "使用范围不能为空")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String useScope;

    /**
     * VIP等级使用范围
     */
    @Schema(description = "VIP使用范围 多个值中间逗号分隔")
    private String vipGradeUseScope;

    /**
     * 同类型权重
     */
    @Schema(description = "同类型权重")
   // @NotNull(message = "同类型权重不能为空")
    private Integer weight;


    /**
     * 备注
     */
    @Schema(description = "备注")
    private String memo;



    @Schema(description = "密钥")
    private String secretKey;

    @Schema(description = "回调url")
    private String callbackUrl;

    @Schema(description = "支付api域名")
    private String apiUrl;

    /**
     * 扩展参数 JSON字符串
     */
    @Schema(description = "扩展参数 JSON字符串")
    private String extParam;

}
