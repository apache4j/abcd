package com.cloud.baowang.common.core.utils;


import cn.hutool.core.util.ObjectUtil;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class BigDecimalUtils {

    private static final DecimalFormat df = new DecimalFormat("0.0000");

    static {
        // 设置为直接截断，不做四舍五入
        df.setRoundingMode(RoundingMode.DOWN);
    }

    /**
     * 加總，並乎略為NULL的參數
     *
     * @param numbers BigDecimal 數組
     * @return summary
     */
    public static BigDecimal summary(BigDecimal... numbers) {
        BigDecimal result = BigDecimal.ZERO;
        if (ObjectUtil.isNotEmpty(numbers)) {
            for (BigDecimal number : numbers) {
                if (ObjectUtil.isNotEmpty(number)) {
                    result = result.add(number);
                }
            }
        }
        return result;
    }

    /**
     * 是否為正數
     *
     * @param number 需進行判斷之BigDecimal數字
     * @return 回傳是否為正數
     */
    public static boolean isPositive(final BigDecimal number) {
        return BigDecimal.ZERO.compareTo(number) < 0;
    }

    /**
     * 判斷數字1是否等於數字2
     *
     * @param numberOne 數字1
     * @param numberTwo 數字2
     * @return 若數字1等於數字2回傳true
     */
    public static boolean eq(BigDecimal numberOne, BigDecimal numberTwo) {
        return numberOne.compareTo(numberTwo) == 0;
    }

    /**
     * 判斷數字1是否大於數字2
     *
     * @param numberOne 數字1
     * @param numberTwo 數字2
     * @return 若數字1大於數字2回傳true
     */
    public static boolean gt(BigDecimal numberOne, BigDecimal numberTwo) {
        return numberOne.compareTo(numberTwo) > 0;
    }

    /**
     * 判斷數字1是否小於數字2
     *
     * @param numberOne 數字1
     * @param numberTwo 數字2
     * @return 若數字1大於數字2回傳true
     */
    public static boolean lt(BigDecimal numberOne, BigDecimal numberTwo) {
        return numberOne.compareTo(numberTwo) < 0;
    }

    /**
     * 额度 * 1000
     * @param amount
     * @return
     */
    public static BigDecimal toK(BigDecimal amount){
        if (amount == null){
            return BigDecimal.ZERO;
        }
        return amount.multiply(new BigDecimal(1000));
    }

    /**
     * 额度 / 1000
     * @param amount
     * @return
     */
    public static BigDecimal k2One(BigDecimal amount){
        return k2One(amount, 0);
    }
    /**
     * 额度 / 1000
     * @param amount
     * @return
     */
    public static BigDecimal k2One(BigDecimal amount, Integer scale){
        if (amount == null){
            return BigDecimal.ZERO;
        }
        return amount.divide(new BigDecimal(1000), RoundingMode.DOWN).setScale(scale,RoundingMode.DOWN);
    }

    /**
     * 金额格式化
     * @param sourceAmount
     * @return
     */
    public static String formatVal(BigDecimal sourceAmount) {
        DecimalFormat df = new DecimalFormat("###,###.00");
        df.setRoundingMode(RoundingMode.DOWN);
        return df.format(sourceAmount);
    }


    public static String formatFourVal(BigDecimal sourceAmount) {
        DecimalFormat df = new DecimalFormat("###,###.0000");
        df.setRoundingMode(RoundingMode.DOWN);
        return df.format(sourceAmount);
    }

    public static String formatFourVal(String sourceAmount) {
        if(StringUtils.isNotEmpty(sourceAmount)){
            BigDecimal big = new BigDecimal(sourceAmount);
            DecimalFormat df = new DecimalFormat("###,###.0000");
            df.setRoundingMode(RoundingMode.DOWN);
            return df.format(big);
        }else{
            return sourceAmount;
        }
    }

    /**
     * 设置截断,保留4位小数
     */
    public static BigDecimal formatFourKeep4Dec(BigDecimal sourceAmount) {
        if(sourceAmount == null){
            sourceAmount = BigDecimal.ZERO;
        }
        return sourceAmount.setScale(4, RoundingMode.DOWN);
    }

    public static BigDecimal formatFourKeep2Dec(BigDecimal sourceAmount) {
        if(sourceAmount == null){
            sourceAmount = BigDecimal.ZERO;
        }
        return sourceAmount.setScale(2, RoundingMode.DOWN);
    }


    /**
     * 设置截断,保留4位小数
     */
    public static String formatFourKeep4DecToStr(BigDecimal sourceAmount) {
        BigDecimal amount = formatFourKeep4Dec(sourceAmount);
        return df.format(amount);
    }



//    public static void main(String[] args) {
//        System.err.println(formatFourKeep4Dec(new BigDecimal("1209210.134324")));
//    }
}
