package com.cloud.baowang.system.po.exchange;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: kimi
 */
@Data
@Builder
public class CalculateRateVO {
    // 三方最后一次汇率
    private BigDecimal lastThirdRate;
   // 汇率调整方式不能为空
    private String adjustWay;
    //调整值
    private String adjustNum;
    //精度 法币 2位 截取 虚拟币 8位 截取
    private int decimalLength;
}
