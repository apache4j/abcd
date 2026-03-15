package com.cloud.baowang.common.core.utils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConvertBeanUtil {
    /**
     * list转key value，key是传入字段，value是当前对象
     * @param list 对象集合
     * @param keyExtractor 作为map的某个key
     * @return Map<list中的某个key,当前对象>
     * @param <T>
     * @param <K>
     */
    public static <T, K> Map<K, T> listToMap(List<T> list, Function<? super T, ? extends K> keyExtractor) {
        return list.stream().collect(Collectors.toMap(keyExtractor, Function.identity()));
    }
}
