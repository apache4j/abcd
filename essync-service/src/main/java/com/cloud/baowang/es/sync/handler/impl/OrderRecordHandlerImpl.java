package com.cloud.baowang.es.sync.handler.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.StopWatch;
import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.es.sync.constants.TableNameConstant;
import com.cloud.baowang.es.sync.enums.TableNameEnum;
import com.cloud.baowang.es.sync.handler.EntryHandler;
import com.cloud.baowang.es.sync.model.OrderRecord;
import com.cloud.baowang.es.sync.properties.CanalKafkaProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component(TableNameConstant.ORDER_RECORD)
public class OrderRecordHandlerImpl implements EntryHandler {
    @Autowired
    private ElasticsearchAsyncClient elasticsearchAsyncClient;
    @Autowired
    private ElasticsearchClient elasticsearchClient;
    @Autowired
    CanalKafkaProperties canalKafkaProperties;

    @Override
    public void process(List<OrderRecord> list, CanalEntry.EventType eventType) {
        try {
            BulkRequest.Builder br = new BulkRequest.Builder();
//            switch (eventType) {
//                case INSERT, UPDATE -> list.forEach(orderRecord -> {
//                    String yearMonth = DateUtils.formatToDateTime(orderRecord.getBetTime(), DateUtils.DATE_FORMAT_3);
//                    br.operations(op -> op.update(u -> u.index(TableNameEnum.ORDER_RECORD.getSource() + CommonConstant.UNDERLINE + yearMonth)
//                            .action(a -> a.docAsUpsert(true).doc(orderRecord)).id(orderRecord.getId())));
//
//                });
//                case DELETE -> list.forEach(orderRecord -> {
//                    String yearMonth = DateUtils.formatToDateTime(orderRecord.getBetTime(), DateUtils.DATE_FORMAT_3);
//                    br.operations(op -> op.delete(u -> u.index(TableNameEnum.ORDER_RECORD.getSource() + CommonConstant.UNDERLINE + yearMonth).id(orderRecord.getId())));
//                });
//                default -> log.info("未知消息类型 {} 不处理 {}", eventType, list);
//            }

            switch (eventType) {
                case INSERT -> list.forEach(orderRecord -> {
                    String yearMonth = DateUtils.formatToDateTime(orderRecord.getBetTime(), DateUtils.DATE_FORMAT_3);
                    br.operations(op -> op.index(i -> i
                            .index(TableNameEnum.ORDER_RECORD.getSource() + CommonConstant.UNDERLINE + yearMonth)
                            .id(orderRecord.getId())
                            .document(orderRecord) // 全量写入，带上所有字段
                    ));
                });

                case UPDATE -> list.forEach(orderRecord -> {
                    String yearMonth = DateUtils.formatToDateTime(orderRecord.getBetTime(), DateUtils.DATE_FORMAT_3);
                    br.operations(op -> op.update(u -> u
                            .index(TableNameEnum.ORDER_RECORD.getSource() + CommonConstant.UNDERLINE + yearMonth)
                            .id(orderRecord.getId())
                            .action(a -> a
                                    .docAsUpsert(true)   // 如果不存在就插入
                                    .doc(orderRecord)    // 部分更新
                            )
                    ));
                });

                case DELETE -> list.forEach(orderRecord -> {
                    String yearMonth = DateUtils.formatToDateTime(orderRecord.getBetTime(), DateUtils.DATE_FORMAT_3);
                    br.operations(op -> op.delete(d -> d
                            .index(TableNameEnum.ORDER_RECORD.getSource() + CommonConstant.UNDERLINE + yearMonth)
                            .id(orderRecord.getId())
                    ));
                });

                default -> log.info("未知消息类型 {} 不处理 {}", eventType, list);
            }


            BulkRequest build = br.build();
            if (CollUtil.isNotEmpty(build.operations())) {
                StopWatch watch = new StopWatch();
                watch.start();
                if (canalKafkaProperties.getIsAsync()) {
                    CompletableFuture<BulkResponse> bulk = elasticsearchAsyncClient.bulk(build);
                    BulkResponse bulkResponse = bulk.get();
                    log.debug("es异步返回,bulkResponse:{}", bulkResponse);
                } else {
                    BulkResponse bulk = elasticsearchClient.bulk(build);
                    if (bulk.errors()){
                        log.error("es同步异常,返回bulk:{}", bulk);
                    }
                }
                watch.stop();
                log.debug("调用es api 更新数据条数:{} 耗时:{}ms", build.operations().size(), watch.getTotalTimeMillis());
            }
        } catch (Exception e) {
            log.error("数据同步至es异常,data:{},error:", list, e);
            throw new RuntimeException(e);
        }

    }
}
