package com.cloud.baowang.es.sync.handler;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.cloud.baowang.es.sync.model.OrderRecord;

import java.util.List;
import java.util.Map;

/**
 * 行级处理器
 */
public interface EntryHandler {

    void process(List<OrderRecord> list, CanalEntry.EventType eventType);
}