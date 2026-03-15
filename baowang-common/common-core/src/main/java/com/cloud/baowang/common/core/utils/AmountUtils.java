package com.cloud.baowang.common.core.utils;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/9/27 15:08
 * @Version: V1.0
 **/
@Slf4j
public class AmountUtils {

    public static void main(String[] args) {
        BigDecimal actRebateAmount=AmountUtils.divide(new BigDecimal("0.012"),new BigDecimal("2.42")).multiply(new BigDecimal("2.42"));
        System.err.println(actRebateAmount);

    }

    /**
     * 按照汇率转换金额(平台币转法币)
     * @param sourceAmount 来源金额
     * @param rate 汇率
     * @return
     */
    public static BigDecimal multiply(BigDecimal sourceAmount,BigDecimal rate){
        if(sourceAmount==null){
            return BigDecimal.ZERO;
        }
        BigDecimal resultAmt=  sourceAmount.multiply(rate).setScale(4,RoundingMode.DOWN);
        log.debug("multiply 来源金额:{},汇率:{},平台币转法币后结果:{}",sourceAmount,rate,resultAmt);
        return resultAmt;
    }

    /**
     * 按照汇率转换金额(平台币转法币)
     * @param sourceAmount 来源金额
     * @param rate 汇率
     * @parm scale 小数位数
     * @return
     */
    public static BigDecimal multiply(BigDecimal sourceAmount,BigDecimal rate,int scale){
        if(sourceAmount==null){
            return BigDecimal.ZERO;
        }
        BigDecimal resultAmt=  sourceAmount.multiply(rate).setScale(scale,RoundingMode.DOWN);
        log.debug("multiply scale 来源金额:{},汇率:{},平台币转法币后结果:{}",sourceAmount,rate,resultAmt);
        return resultAmt;
    }

    /**
     * 按照汇率转换金额 (法币转平台币)
     * @param sourceAmount 来源金额
     * @param rate 汇率
     * @return
     */
    public static BigDecimal divide(BigDecimal sourceAmount,BigDecimal rate){
        if(sourceAmount==null){
            return BigDecimal.ZERO;
        }
        if(rate==null||rate.compareTo(BigDecimal.ZERO)==0){
            log.debug("divide 法币转平台币 转换汇率:{}不合法,无法计算",rate);
            return BigDecimal.ZERO;
        }
        BigDecimal resultAmt=sourceAmount.divide(rate,4,RoundingMode.DOWN);
        log.debug("divide 来源金额:{},汇率:{},法币转平台币后结果:{}",sourceAmount,rate,resultAmt);
        return resultAmt;
    }
    /**
     * 按照汇率转换金额 (法币转平台币) 不保留2位小数
     * @param sourceAmount 来源金额
     * @param rate 汇率
     * @return
     */
    public static BigDecimal divide2(BigDecimal sourceAmount,BigDecimal rate){
        if(sourceAmount==null){
            return BigDecimal.ZERO;
        }
        if(rate==null||rate.compareTo(BigDecimal.ZERO)==0){
            log.debug("divide2 法币转平台币 转换汇率:{}不合法,无法计算",rate);
            return BigDecimal.ZERO;
        }
        BigDecimal resultAmt=sourceAmount.divide(rate,8,RoundingMode.DOWN);
        log.debug("divide2 来源金额:{},汇率:{},法币转平台币后结果:{}",sourceAmount,rate,resultAmt);
        return resultAmt;
    }


    /**
     * 除法 计算 比例
     * @param sourceAmount
     * @param sourceDivideAmount
     * @param scale
     * @return
     */
    public static BigDecimal divide(BigDecimal sourceAmount,BigDecimal sourceDivideAmount,int scale){
        if(sourceAmount==null){
            return BigDecimal.ZERO;
        }
        if(sourceDivideAmount==null||sourceDivideAmount.compareTo(BigDecimal.ZERO)==0){
            log.debug("divide 比例计算:{}不合法,无法计算",sourceDivideAmount);
            return BigDecimal.ZERO;
        }
        return sourceAmount.divide(sourceDivideAmount,scale,RoundingMode.DOWN);
    }

    /**
     * 按照汇率转换金额 (法币转另一种法币)
     *
     * @param sourceAmount 源金额
     * @param rate         源汇率
     * @param targetRate   目标汇率
     * @return
     */
    public static BigDecimal transfer(BigDecimal sourceAmount, BigDecimal rate, BigDecimal targetRate) {
        if (sourceAmount == null || sourceAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        if (rate == null || rate.compareTo(BigDecimal.ZERO) == 0) {
            log.info("transfer 法币转另一种法币 转换汇率:{},目标汇率:{},无法计算", rate, targetRate);
            return BigDecimal.ZERO;
        }
        return sourceAmount.divide(rate, 8, RoundingMode.DOWN).multiply(targetRate).setScale(4, RoundingMode.DOWN);
    }

    /**
     * 按照汇率转换金额 (法币转另一种法币) 不保留两位小数
     *
     * @param sourceAmount 源金额
     * @param rate         源汇率
     * @param targetRate   目标汇率
     * @return
     */
    public static BigDecimal transfer2(BigDecimal sourceAmount, BigDecimal rate, BigDecimal targetRate) {
        if (sourceAmount == null || sourceAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        if (rate == null || rate.compareTo(BigDecimal.ZERO) == 0) {
            log.info("法币转另一种法币 转换汇率:{},目标汇率:{},无法计算", rate, targetRate);
            return BigDecimal.ZERO;
        }
        return sourceAmount.divide(rate, 8, RoundingMode.DOWN).multiply(targetRate);
    }


    /**
     * 按照汇率转换金额
     * @param sourceAmount 来源金额
     * @param sourcePercentRate 百分比汇率
     * @return
     */
    public static BigDecimal multiplyPercent(BigDecimal sourceAmount,BigDecimal sourcePercentRate){
        if(sourceAmount==null){
            return BigDecimal.ZERO;
        }
        // 不固定精度地转换成小数（percentRate），保留原始精度
        BigDecimal percentRate = sourcePercentRate.divide(new BigDecimal("100"));
        // 金额 * 百分比，并保留两位小数，向下舍入
        return sourceAmount.multiply(percentRate).setScale(4, RoundingMode.DOWN);
    }

    /**
     * 按照汇率转换金额
     * @param sourceAmount 来源金额
     * @param percentRate 百分比汇率
     * @return 转换后金额
     */
    public static BigDecimal dividePercent(BigDecimal sourceAmount,BigDecimal percentRate){
        if(sourceAmount==null){
            return BigDecimal.ZERO;
        }
        if(percentRate==null||percentRate.compareTo(BigDecimal.ZERO)==0){
            log.info("按照汇率转换金额 转换汇率:{}不合法",percentRate);
            return null;
        }
        BigDecimal percent=percentRate.divide(new BigDecimal("100"),4,RoundingMode.CEILING);
        return sourceAmount.divide(percent,4,RoundingMode.DOWN);
    }

    public static String format(BigDecimal sourceAmount) {
        if(sourceAmount==null){
            return null;
        }
        DecimalFormat df = new DecimalFormat("#.00");
        df.setRoundingMode(RoundingMode.DOWN);
        return df.format(sourceAmount);
    }

    /**
     * 百分比展示
     * @param sourceAmount 原始金额 例如 0.0568
     * @return 字符串 例如 5.68%
     */
    public static String formatPercent(BigDecimal sourceAmount) {
        if(sourceAmount==null||sourceAmount.compareTo(BigDecimal.ZERO)==0){
            return "0.00%";
        }
        BigDecimal resultAmount=sourceAmount.multiply(new BigDecimal("100"));
        DecimalFormat df = new DecimalFormat("#.00");
        df.setRoundingMode(RoundingMode.DOWN);
        return df.format(resultAmount).concat("%");
    }

    /*public static void main(String[] args) {
        System.err.println(AmountUtils.format(new BigDecimal("50000.06982223")));
        System.err.println(AmountUtils.formatPercent(new BigDecimal("6.3981")));
    }*/
}
