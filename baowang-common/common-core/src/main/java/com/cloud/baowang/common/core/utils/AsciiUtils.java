package com.cloud.baowang.common.core.utils;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2025/6/28 10:53
 * @Version: V1.0
 **/
public class AsciiUtils {
   /* public static void main(String[] args) {
        Long selfCenterId=AsciiUtils.getAsciiValues("OwlgYv");
        System.out.println(SnowFlakeUtils.getSnowIdBySelfCenterId(selfCenterId));
        Long selfCenterId1=AsciiUtils.getAsciiValues("Abcdef");
        System.out.println(SnowFlakeUtils.getSnowIdBySelfCenterId(selfCenterId1));
    }
*/
    /**
     * 返回字符串中每个字符的 ASCII 值数组
     */
    public static Long getAsciiValues(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        StringBuilder stb=new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            int num=input.charAt(i);
            stb.append(num+"");
        }
        return Long.valueOf(stb.toString());
    }

    /**
     * 打印字符串中每个字符的 ASCII 值（仅调试用）
     */
    static void printAsciiValues(String input) {
        Long values = getAsciiValues(input);
        System.out.println(values);
    }

}
