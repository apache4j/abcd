package com.cloud.baowang.play.game.base;



import com.cloud.baowang.common.core.utils.ListPartitionUtil;

import java.util.List;
import java.util.Objects;

/**
 * 三方注单拉取分区组件
 */
public class ThirdPullBetPartitionComponent {

    /**
     * 按不同数量进行分区适配
     *
     * @param betRecords
     * @param count
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> partition(List<T> betRecords, Long count) {
        List<List<T>> lists;
        if (Objects.nonNull(count)) {
            if (count >= 10000) {
                lists = ListPartitionUtil.partition(betRecords, 2000);
            } else if (count >= 5000) {
                lists = ListPartitionUtil.partition(betRecords, 1500);
            } else if (count >= 3000) {
                lists = ListPartitionUtil.partition(betRecords, 800);
            } else if (count >= 1000) {
                lists = ListPartitionUtil.partition(betRecords, 500);
            } else if (count >= 500) {
                lists = ListPartitionUtil.partition(betRecords, 300);
            } else {
                lists = ListPartitionUtil.partition(betRecords, 200);
            }
        } else {
            lists = ListPartitionUtil.partition(betRecords, 500);
        }
        return lists;
    }
}
