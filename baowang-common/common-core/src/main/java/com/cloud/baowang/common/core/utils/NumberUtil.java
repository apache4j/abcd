package com.cloud.baowang.common.core.utils;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
public class NumberUtil {

    public static final List<String> all_abc = Lists.newArrayList(
            "q", "w", "e", "r", "t", "y", "u", "i", "o", "p",
            "a", "s", "d", "f", "g", "h", "j", "k", "l",
            "z", "x", "c", "v", "b", "n", "m");

    public static final List<String> ALL_ABC = Lists.newArrayList(
            "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
            "A", "S", "D", "F", "G", "H", "J", "K", "L",
            "Z", "X", "C", "V", "B", "N", "M");

    public static final List<String> ALL_NUM = Lists.newArrayList(
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9");

    public static int getRandom(int min, int max) {
        return (int) (min + Math.random() * (max - min + 1));
    }

    /**
     * 自动生成 支付平台号
     */
    public static String autoGeneratePlatformCode() {
        String platformCode = "";
        for (int i = 0; i < 3; i++) {
            int index = NumberUtil.getRandom(0, ALL_ABC.size() - 1);
            String str = ALL_ABC.get(index);
            platformCode = platformCode.concat(str);
        }
        for (int i = 0; i < 3; i++) {
            int index = NumberUtil.getRandom(0, all_abc.size() - 1);
            String str = all_abc.get(index);
            platformCode = platformCode.concat(str);
        }
        for (int i = 0; i < 5; i++) {
            int index = NumberUtil.getRandom(0, ALL_NUM.size() - 1);
            String str = ALL_NUM.get(index);
            platformCode = platformCode.concat(str);
        }
        return platformCode;
    }

    public static int getNumberOfDecimalPlace(BigDecimal value) {

        final String s = value.toPlainString();
        final int index = s.indexOf('.');
        if (index < 0) {
            return 0;

        }
        return s.length() - 1 - index;
    }

    public static void main(String[] args) {
        int numberOfDecimalPlace = getNumberOfDecimalPlace(new BigDecimal("10.1"));

        int numberOfDecimalPlace1 = getNumberOfDecimalPlace(new BigDecimal("10.1114"));
        System.out.println(numberOfDecimalPlace + "-" + numberOfDecimalPlace1);
    }


    /**
     * 除法 获取比例
     * @param firstNum 分子
     * @param secondNum  分母
     * @return
     */
    public static BigDecimal divide(Long firstNum, Long secondNum,int scaleNum) {
        if(secondNum==null||secondNum==0){
            return BigDecimal.ZERO;
        }
        BigDecimal firstNumDecimal=BigDecimal.valueOf(firstNum);
        BigDecimal secondNumDecimal=BigDecimal.valueOf(secondNum);
        return firstNumDecimal.divide(secondNumDecimal,scaleNum, RoundingMode.DOWN);
    }

}
