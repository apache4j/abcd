package com.cloud.baowang.common.core.utils;

import cn.hutool.core.lang.Pair;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Slf4j
public class CharArrayUtil {

    public static String getChars(char[] charArray, int... indices) {
        StringBuilder builder = new StringBuilder();
        for (int index : indices) {
            if (index >= 0 && index < charArray.length) {
                builder.append(charArray[index]);
            }
        }
        return builder.toString();
    }


    public static Pair<String, String> extractAndMerge(String original, int... indexes) {
        StringBuilder extractedText = new StringBuilder();
        StringBuilder remainingText = new StringBuilder(original);

        // 遍历索引数组
        for (int i = indexes.length - 1; i >= 0; i--) {
            int index = indexes[i];
            char ca = original.charAt(index);

            // 提取指定索引位置的字符
            extractedText.append(ca);

            // 在余下的字符串中删除已提取的字符
            remainingText.deleteCharAt(index);

        }

        return new Pair<>(extractedText.reverse().toString(), remainingText.toString());
    }

    /**
     * 转换成 hashmap key可以重复， 解决jdk自带的toMap会报duplicateException
     *
     * @param keyMapper
     * @param valueMapper
     * @param <T>
     * @param <K>
     * @param <U>
     * @return
     */
    public static <T, K, U>
    Collector<T, ?, Map<K, List<U>>> toHashMap(Function<? super T, ? extends K> keyMapper,
                                               Function<? super T, ? extends U> valueMapper) {
        return Collectors.toMap(keyMapper,
                t -> {
                    List<U> list = new ArrayList<>();
                    list.add(valueMapper.apply(t));
                    return list;
                },
                (o, o2) -> {
                    o.addAll(o2);
                    return o;
                },
                HashMap::new);
    }


    public static void main(String[] args) {
        char[] charArray = {'H', 'e', 'l', 'l', 'o', ',', ' ', 'w', 'o', 'r', 'l', 'd', '!'};
        int[] indices = {0, 4, 5, 6, 11, 12};

        String result = getChars(charArray, indices);
        System.out.println("Result: " + result); // Output: Hello,!
    }
}
