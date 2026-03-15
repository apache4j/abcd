package com.cloud.baowang.common.core.utils;

import com.google.common.collect.Lists;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class ListConverter {

    /**
     * Java对list进行分页，subList()方法实现分页
     *
     * @param list
     * @param pageNum
     * @param pageSize
     * @return
     */
    public static List subPage(List list, Integer pageNum, Integer pageSize) {
        if (CollectionUtils.isEmpty(list)) {
            return Lists.newArrayList();
        }
        if (list.size() == 0) {
            return Lists.newArrayList();
        }
        //记录总数
        Integer count = list.size();
        //开始索引
        int fromIndex = (pageNum - 1) * pageSize;
        //结束索引
        int toIndex = pageNum * pageSize;
        if (fromIndex + 1 > count) {
            return Lists.newArrayList();
        }
        if (pageNum * pageSize > count) {
            toIndex = count;
        }
        List pageList = list.subList(fromIndex, toIndex);
        return pageList;
    }

}
