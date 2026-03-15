package com.cloud.baowang.es.sync.handler.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.StopWatch;
import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.FlatMessage;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.es.sync.enums.TableNameEnum;
import com.cloud.baowang.es.sync.handler.EntryHandler;
import com.cloud.baowang.es.sync.handler.MessageHandler;
import com.cloud.baowang.es.sync.model.OrderRecord;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class EsMessageHandlerImpl implements MessageHandler<FlatMessage> {

    private final Map<String, EntryHandler> entryHandlerMap;


    public EsMessageHandlerImpl(Map<String, EntryHandler> entryHandlerMap) {
        this.entryHandlerMap = entryHandlerMap;
    }

    @Override
    public void handleMessage(List<FlatMessage> messages) {
        log.info("开始同步本次数据,条数:{}", messages.size());
        StopWatch watch = new StopWatch();
        watch.start();
        // update
        LinkedList<OrderRecord> upsertMessageList = Lists.newLinkedList();

        //  insert
        LinkedList<OrderRecord> insertMessageList = Lists.newLinkedList();

        // delete
        LinkedList<OrderRecord> deleteMessageList = Lists.newLinkedList();
        messages.forEach(flatMessage -> {
            if (Optional.ofNullable(flatMessage).map(FlatMessage::getData).isEmpty()) {
                return;
            }
            List<OrderRecord> orderRecordList = flatMessage.getData().stream().filter(CollUtil::isNotEmpty).map(s -> JSON.parseObject(JSON.toJSONString(s), OrderRecord.class)).toList();
            if (CollUtil.isNotEmpty(orderRecordList)) {
                Map<String, List<OrderRecord>> listMap = orderRecordList.stream().collect(Collectors.groupingBy(OrderRecord::getId));
                CanalEntry.EventType eventType = CanalEntry.EventType.valueOf(flatMessage.getType());
                listMap.forEach((id, list) -> {
                    if (CollUtil.isNotEmpty(list) && list.size() > CommonConstant.business_one) {
                        executeTask(list, flatMessage.getTable(), eventType);
                    } else {
                        switch (eventType) {
                            case INSERT -> insertMessageList.addAll(list);
                            case UPDATE -> upsertMessageList.addAll(list);
                            case DELETE -> deleteMessageList.addAll(list);
                        }
                    }
                });
            }
        });
        if (CollUtil.isNotEmpty(upsertMessageList)) {
            executeTask(upsertMessageList, TableNameEnum.ORDER_RECORD.source, CanalEntry.EventType.UPDATE);
        }
        if (CollUtil.isNotEmpty(insertMessageList)) {
            executeTask(insertMessageList, TableNameEnum.ORDER_RECORD.source, CanalEntry.EventType.INSERT);
        }
        if (CollUtil.isNotEmpty(deleteMessageList)) {
            executeTask(deleteMessageList, TableNameEnum.ORDER_RECORD.source, CanalEntry.EventType.DELETE);
        }
        watch.stop();
        log.info("本次同步消息数:{},耗时:{}ms", messages.size(), watch.getTotalTimeMillis());
    }


    public void executeTask(List<OrderRecord> list, String table, CanalEntry.EventType eventType) {
        EntryHandler entryHandler = entryHandlerMap.get(table);
        if (Objects.isNull(entryHandler)) {
            log.error("该表无映射关系,无法同步,tableName:{}", table);
            return;
        }
        log.info("消息发送至行级批量处理 {}", eventType);
        entryHandler.process(list, eventType);
        list.clear();
    }
}
