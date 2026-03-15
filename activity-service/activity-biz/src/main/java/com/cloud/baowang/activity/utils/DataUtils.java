package com.cloud.baowang.activity.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class DataUtils {
    /**
     * 将String数组转换为逗号分隔的字符串
     *
     * @param array 需要转换的字符串数组
     * @return 逗号分隔的字符串，如果数组为空或为null，则返回空字符串
     */
    public static String arrayToString(String[] array) {
        if (array == null || array.length == 0) {
            return "";
        }
        // 使用 String.join 将数组元素用逗号连接
        return String.join(",", array);
    }

    /**
     * 将逗号分隔的字符串转换为String数组
     *
     * @param str 需要转换的逗号分隔的字符串
     * @return 字符串数组，如果字符串为空或为null，则返回空数组
     */
    public static String[] stringToArray(String str) {
        if (str == null || str.trim().isEmpty()) {
            return new String[0];
        }
        // 使用 split 方法按逗号分隔字符串并转换为数组
        return str.split(",");
    }

    /**
     * 将List<String>转换为逗号分隔的字符串
     *
     * @param list 需要转换的字符串列表
     * @return 逗号分隔的字符串，如果列表为空或为null，则返回空字符串
     */
    public static String listToString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        // 使用 String.join 将列表元素用逗号连接
        return String.join(",", list);
    }

    /**
     * 将逗号分隔的字符串转换为List<String>
     *
     * @param str 需要转换的逗号分隔的字符串
     * @return 字符串列表，如果字符串为空或为null，则返回空列表
     */
    public static List<String> stringToList(String str) {
        if (str == null || str.trim().isEmpty()) {
            return Collections.emptyList();
        }
        // 使用 split 方法按逗号分隔字符串并转换为列表
        return Arrays.asList(str.split(","));
    }
    /**
     * 检查两个逗号分隔的字符串是否包含相同的元素（无序、去重后比较）
     *
     * @param str1 第一个字符串，例如 "1,2,3,4"
     * @param str2 第二个字符串，例如 "2,3,4,1"
     * @return 如果两个字符串的元素相同（不考虑顺序），则返回 true；否则返回 false
     */
    public static boolean checkStringSame(String str1, String str2) {
        // 处理空值情况，如果其中一个为空，则不可能相等
        if (str1 == null || str2 == null) {
            return false;
        }

        // 将字符串按照逗号分割，并去掉可能的空格，然后转换为 Set（去重）
        Set<String> set1 = Arrays.stream(str1.split(","))  // 按逗号分割
                .map(String::trim)       // 去掉前后空格，防止误差
                .collect(Collectors.toSet()); // 转换为 Set（自动去重）

        Set<String> set2 = Arrays.stream(str2.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());

        // 使用 Set 的 equals 方法进行比较，如果两个集合相等，则返回 true
        return set1.equals(set2);
    }


}
