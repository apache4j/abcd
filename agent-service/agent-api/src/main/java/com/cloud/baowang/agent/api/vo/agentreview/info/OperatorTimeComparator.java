package com.cloud.baowang.agent.api.vo.agentreview.info;


import com.cloud.baowang.agent.api.vo.agentreview.info.ShortUrlChangeRecordPageVO;

import java.util.Comparator;

/**
 * @className: OperatorTimeComparator
 * @author: wade
 * @description: 排序规则
 * @date: 2024/4/8 15:45
 */
public class OperatorTimeComparator implements Comparator<ShortUrlChangeRecordPageVO> {
    /**
     * 指示排序方式，true 为升序，false 为降序
     */
    private final boolean ascending;

    /**
     * @param ascending 指示排序方式，true 为升序，false 为降序
     */
    public OperatorTimeComparator(boolean ascending) {
        this.ascending = ascending;
    }

    @Override
    public int compare(ShortUrlChangeRecordPageVO o1, ShortUrlChangeRecordPageVO o2) {
        int result = Long.compare(o1.getOperatorTime(), o2.getOperatorTime());
        return ascending ? result : -result; // 根据排序方式返回结果
    }
}
