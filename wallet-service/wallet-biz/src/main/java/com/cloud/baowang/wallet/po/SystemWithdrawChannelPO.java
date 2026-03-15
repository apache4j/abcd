package com.cloud.baowang.wallet.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * <p>
 * 提现通道配置
 * </p>
 *
 * @author qiqi
 */
@Getter
@Setter
@TableName("system_withdraw_channel")
public class SystemWithdrawChannelPO extends BasePO {

    /**
     * 提现通道编号
     */
    private String withdrawChannelNo;

    /**
     * 货币代码
     */
    private String currencyCode;

    /**
     * 通道类型
     */
    private String channelType;
    /**
     * 提款类型Code
     */
    private String withdrawTypeId;

    /**
     * 提款类型编码
     */
    private String withdrawTypeCode;

    /**
     * 提款方式Id
     */
    private String withdrawWayId;

    /**
     * 通道代码
     */
    private String channelCode;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 排序
     */
    private Integer sortOrder;


    /**
     * 商户号
     */
    private String merNo;
    /**
     * 公钥
     */
    private String pubKey;
    /**
     * 密钥
     */
    private String privateKey;


    /**
     * 密钥
     */
    private String secretKey;

    /**
     * 提现最小值
     */
    private BigDecimal withdrawMin;

    /**
     * 提现最大值
     */
    private BigDecimal withdrawMax;

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

//    /**
//     * 扩展参数 JSON字符串
//     */
//    private String extParam;


}
