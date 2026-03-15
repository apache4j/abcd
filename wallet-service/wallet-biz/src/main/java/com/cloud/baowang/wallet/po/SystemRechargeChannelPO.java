package com.cloud.baowang.wallet.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * <p>
 * 充值通道配置
 * </p>
 *
 * @author ford
 * @since 2024-07-26 11:50:55
 */
@Getter
@Setter
@TableName("system_recharge_channel")
public class SystemRechargeChannelPO extends BasePO {

    /**
     * 充值通道编号
     */
    private String rechargeChannelNo;

    /**
     * 货币代码
     */
    private String currencyCode;

    /**
     * 通道类型
     */
    private String channelType;

    /**
     * 充值类型Id
     */
    private String rechargeTypeId;

    /**
     * 充值类型编码
     */
    private String rechargeTypeCode;

    /**
     * 充值方式Id
     */
    private String rechargeWayId;

    /**
     * 通道代码
     */
    private String channelCode;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 商户号
     */
    private String merNo;
    /**
     * 公钥
     */
    private String pubKey;
    /**
     * 私钥
     */
    private String privateKey;

    /**
     * 密钥
     */
    private String secretKey;

    /**
     * 排序
     */
    private Integer sortOrder;


    /**
     * 充值最小值
     */
    private BigDecimal rechargeMin;

    /**
     * 充值最大值
     */
    private BigDecimal rechargeMax;

    /**
     * 通道手续费
     */
    private BigDecimal fee;

    /**
     * 使用范围
     */
    private String useScope;

    /**
     * 同类型权重
     */
    private Integer weight;

    /**
     * 授权数量
     */
    private Integer authNum;

    /**
     * 备注
     */
    private String memo;

    /**
     * 状态 0:禁用 1:启用
     */
    private Integer status;

    /**
     * 回调url
     */
    private String callbackUrl;

    /**
     * 支付api域名
     */
    private String apiUrl;

    /**
     * VIP等级使用范围
     */
    private String vipGradeUseScope;

    /**
     * 扩展参数 JSON字符串
     */
    private String extParam;




}
