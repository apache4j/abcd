package com.cloud.baowang.common.es.util;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.time.format.DateTimeFormatter;

/**
 * @Author: sheldon
 * @Date: 3/18/24 11:51 上午
 */
public class OrderBoolQueryUtil {

    private final static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private final static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 设置订单查询条件
     * @return
     */
    public static BoolQueryBuilder initParams(Object obj){
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        //todo 此处用于拼接条件参数


        return boolQuery;
    }
}
