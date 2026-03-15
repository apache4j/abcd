package com.cloud.baowang.common.core.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 切分list工具
 */
public class ListPartitionUtil {
    public static <T> List<List<T>> partition(List<T> list, int size) {
        int totalSize = list.size();
        int partitionCount = (int) Math.ceil((double) totalSize / size);

        List<List<T>> partitions = new ArrayList<>(partitionCount);
        for (int i = 0; i < partitionCount; i++) {
            int fromIndex = i * size;
            int toIndex = Math.min(fromIndex + size, totalSize);
            partitions.add(list.subList(fromIndex, toIndex));
        }

        return partitions;
    }
}
