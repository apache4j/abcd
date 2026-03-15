package com.cloud.baowang.common.es.service;

import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.SpringUtils;
import com.cloud.baowang.common.core.vo.base.PageVO;
import com.cloud.baowang.common.es.entity.BaseEntityAssign;
import com.cloud.baowang.common.es.util.PageConvertUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.dromara.easyes.annotation.IndexName;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.PipelineAggregatorBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedAvg;
import org.elasticsearch.search.aggregations.metrics.ParsedSum;
import org.elasticsearch.search.aggregations.metrics.ParsedValueCount;
import org.elasticsearch.search.aggregations.metrics.SumAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ValueCountAggregationBuilder;
import org.elasticsearch.search.aggregations.pipeline.BucketSelectorPipelineAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * E=返回对象类(返回对象类需要继承索引PO对象类),D=入参类
 *
 * @Author: sheldon 需要使用到的查询函数封装基类
 * @Date: 3/18/24 10:22 上午
 */
@Slf4j
public class BaseEsServiceImpl<E extends BaseEntityAssign, D> {
    private static RestHighLevelClient restHighLevelClient;

    @PostConstruct
    public void init() {
        restHighLevelClient = SpringUtils.getBean(RestHighLevelClient.class);
    }

    private static final String NUMBER_FORMAT = "0.0000";
    private Type[] types = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
    private Class<E> clazzE = (Class<E>) types[0];
    private Class<D> clazzD = (Class<D>) types[1];
    //    String esType =  clazzE.getAnnotation(Document.class).type();
    String esIndex = clazzE.getAnnotation(IndexName.class).value();

    public List<E> listByDto(SearchRequest query) {
        List<E> list = Lists.newArrayList();
        try {
            query.indices(esIndex);
            SearchResponse searchResponse = restHighLevelClient.search(query, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();

            for (SearchHit hit : hits) {
                String content = hit.getSourceAsString();
                list.add(JSONObject.parseObject(content, clazzE));
            }
        } catch (Exception e) {
            log.error("com.cloud.baowang.common.es.service.impl.OrderBaseEsServiceImpl.listByDto:query{} error", query, e);
        }
        return list;
    }

    /**
     * 查询分页查询
     * @param queryBuilder
     * @param page 分页
     * @param sortOrder 排序方式
     * @return
     */
    public IPage<E> pageByDto(BoolQueryBuilder queryBuilder, PageVO page, SortBuilder<FieldSortBuilder>... sortOrder) {
        // 排序参数
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        if (ArrayUtil.isNotEmpty(sortOrder)) {
            for (SortBuilder<FieldSortBuilder> sortBuilder : sortOrder) {
                sourceBuilder.sort(sortBuilder);
            }
        }
        Page pageable = PageConvertUtil.getPage(page);
        sourceBuilder.from((int) pageable.getCurrent()) // 从第1条记录开始（默认为0）
                .size((int) pageable.getSize()); // 每次返回10条结果


        sourceBuilder.query(queryBuilder);
        sourceBuilder.trackTotalHits(true);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(sourceBuilder);
        return pageByDto(searchRequest);
    }

    public IPage<E> pageByDto(SearchRequest query) {
        query.indices(esIndex);
        List<E> list = Lists.newArrayList();
        IPage<E> iPage = new Page<>();
        try {
//            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//            searchSourceBuilder.trackTotalHits(true);
//            query.source(searchSourceBuilder);
            SearchResponse searchResponse = restHighLevelClient.search(query, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            long total = hits.getTotalHits().value;
            for (SearchHit hit : hits.getHits()) {
                String content = hit.getSourceAsString();
                list.add(JSONObject.parseObject(content, clazzE));
            }
            iPage.setTotal(total);
            iPage.setRecords(list);
        } catch (Exception e) {
            log.error("com.cloud.baowang.common.es.service.impl.OrderBaseEsServiceImpl.pageByDto :query{} error", query, e);
        }
        return iPage;

    }

    //
    public Long countByDto(BoolQueryBuilder queryBuilder, String countField) {
        try {
            ValueCountAggregationBuilder countBuilder = AggregationBuilders.count("count_" + countField).field(countField);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                    .trackTotalHits(true)
                    .query(queryBuilder)
                    .aggregation(countBuilder);
            SearchRequest searchRequest = new SearchRequest(esIndex).source(sourceBuilder);
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            ParsedValueCount parsedValueCount = response.getAggregations().get("count_" + countField);
            return parsedValueCount.getValue();
        } catch (Exception e) {
            log.error("com.cloud.baowang.common.es.service.impl.OrderBaseEsServiceImpl.countByDto :query{} error", queryBuilder, e);
        }

        return 0L;
    }

    public Long distinctCountByDto(BoolQueryBuilder queryBuilder, String countField) {
        try {

            // 创建一个新的SearchRequest对象
            SearchRequest searchRequest = new SearchRequest(esIndex);

            // 构建查询条件，这里使用match_all查询
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(queryBuilder);
            // 添加聚合查询，对字段"your_field"执行唯一计数
            searchSourceBuilder.aggregation(AggregationBuilders.terms("distinct_count_" + countField).field(countField));
            searchRequest.source(searchSourceBuilder);
            // 执行搜索
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            // 获取聚合结果
            Aggregations aggregations = searchResponse.getAggregations();
            Terms distinctCountAgg = aggregations.get("distinct_count_" + countField);

            // 打印唯一值的数量
            long numDistinct = distinctCountAgg.getBuckets().size();
            System.out.println("Distinct count: " + numDistinct);
        } catch (Exception e) {
            log.error("com.cloud.baowang.common.es.service.impl.OrderBaseEsServiceImpl.distinctCountByDto :query{} error", queryBuilder, e);
        }
        return 0L;
    }

    public Map<String, BigDecimal> sumByDto(BoolQueryBuilder queryBuilder, String... sumField) {
        try {
            SearchRequest searchRequest = new SearchRequest(esIndex);

            // 构建查询条件
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(queryBuilder); // 这里使用了match_all查询，你可以根据需要修改查询条件

            for (String s : sumField) {
                AggregationBuilder builder = AggregationBuilders.sum(s).field(s);
                searchSourceBuilder.aggregation(builder);
            }
            // 设置搜索请求的源代码
            searchRequest.source(searchSourceBuilder);

            // 执行搜索
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Aggregations aggregations = searchResponse.getAggregations();

            HashMap<String, BigDecimal> returnMap = new HashMap<>();

            Map<String, Aggregation> aggregationMap = aggregations.asMap();
            for (String s : sumField) {
                ParsedSum parsedSum = (ParsedSum) aggregationMap.get(s);
                returnMap.put(s, new BigDecimal(parsedSum.getValueAsString()));
            }
            return returnMap;

        } catch (Exception e) {
            log.error("com.cloud.baowang.common.es.service.impl.OrderBaseEsServiceImpl.sumByDto :query{} error", queryBuilder, e);
        }
        return null;
    }

    public Map<String, Map<String, Object>> sumGroupByDto(BoolQueryBuilder queryBuilder, String groupByField, String... sumField) {
        HashMap<String, Map<String, Object>> returnMap = new HashMap<>();

        try {
            SearchRequest searchRequest = new SearchRequest(esIndex);

            // 构建查询条件
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(queryBuilder); // 这里使用了match_all查询，你可以根据需要修改查询条件

            for (String s : sumField) {
                AggregationBuilder builder = AggregationBuilders.sum(s).field(s);
                searchSourceBuilder.aggregation(builder);
            }

            TermsAggregationBuilder groupByBuilder = AggregationBuilders.terms(groupByField).field(groupByField).size(Integer.MAX_VALUE);
            for (String s : sumField) {
                SumAggregationBuilder terms = AggregationBuilders.sum(s).field(s);
                groupByBuilder.subAggregation(terms);
            }
            searchSourceBuilder.aggregation(groupByBuilder);
            // 设置搜索请求的源代码
            searchRequest.source(searchSourceBuilder);

            // 执行搜索
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            Terms byAgeAggregation = searchResponse.getAggregations().get(groupByField);
            for (Terms.Bucket bucket : byAgeAggregation.getBuckets()) {

                HashMap<String, Object> map = Maps.newHashMap();
                String key = bucket.getKeyAsString();
                Aggregations bucketAggregations = bucket.getAggregations();
                Map<String, Aggregation> asMap = bucketAggregations.asMap();
                for (String s : sumField) {
                    ParsedSum sum = (ParsedSum) asMap.get(s);
                    map.put(s, sum.getValue());
                }
                returnMap.put(key, map);
                // 处理结果
            }


        } catch (Exception e) {
            log.error("com.cloud.baowang.common.es.service.impl.OrderBaseEsServiceImpl.sumByDto :query{} error", queryBuilder, e);
        }
        return returnMap;
    }


    public Map<String, Map<String, Object>> countGroupByDto(BoolQueryBuilder queryBuilder, String groupByField, String... countField) {
        Map<String, Map<String, Object>> returnMap = Maps.newHashMap();
        SearchRequest searchRequest = new SearchRequest(esIndex);
        TermsAggregationBuilder groupByBuilder = AggregationBuilders.terms(groupByField).field(groupByField).size(Integer.MAX_VALUE);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);

        if (countField != null) {
            for (String s : countField) {
                ValueCountAggregationBuilder terms = AggregationBuilders.count("count_" + s).field(s);
                groupByBuilder.subAggregation(terms);
            }
        }
        searchSourceBuilder.aggregation(groupByBuilder);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            ParsedLongTerms group = (ParsedLongTerms) searchResponse.getAggregations().asMap().get(groupByField);
            group.getBuckets().forEach(bucket -> {
                HashMap<String, Object> map = Maps.newHashMap();
                String key = bucket.getKeyAsString();
                Map<String, Aggregation> asMap = bucket.getAggregations().asMap();
                if (countField != null) {
                    for (String s : countField) {
                        String tempKey = "count_" + s;
                        ParsedValueCount sum = (ParsedValueCount) asMap.get(tempKey);
                        map.put(tempKey, BigDecimal.valueOf(sum.getValue()));
                    }
                }
                returnMap.put(key, map);
            });
            return returnMap;
        } catch (Exception e) {
            log.error("com.cloud.baowang.common.es.service.impl.OrderBaseEsServiceImpl.countAndSumGroupByDto :query{} error", queryBuilder, e);
        }
        return null;

    }

    public Map<String, Map<String, Object>> countAndSumGroupByDto(BoolQueryBuilder queryBuilder, String groupByField, List<String> countField, List<String> sumField) {
        Map<String, Map<String, Object>> returnMap = Maps.newHashMap();
        SearchRequest searchRequest = new SearchRequest(esIndex);
        TermsAggregationBuilder groupByBuilder = AggregationBuilders.terms(groupByField).field(groupByField).size(Integer.MAX_VALUE);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);

        if (countField != null) {
            for (String s : countField) {
                ValueCountAggregationBuilder terms = AggregationBuilders.count(s).field(s);
                groupByBuilder.subAggregation(terms);
            }
        }
        if (sumField != null) {
            for (String s : sumField) {
                SumAggregationBuilder terms = AggregationBuilders.sum(s).field(s);
                terms.format(NUMBER_FORMAT);
                groupByBuilder.subAggregation(terms);
            }
        }
        searchSourceBuilder.aggregation(groupByBuilder);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            ParsedLongTerms group = (ParsedLongTerms) searchResponse.getAggregations().asMap().get(groupByField);
            group.getBuckets().forEach(bucket -> {
                HashMap<String, Object> map = Maps.newHashMap();
                String key = bucket.getKeyAsString();
                Map<String, Aggregation> asMap = bucket.getAggregations().asMap();
                if (countField != null) {
                    for (String s : countField) {
                        ParsedValueCount count = (ParsedValueCount) asMap.get(s);
                        map.put(s, count.getValue());
                    }
                }
                if (sumField != null) {
                    for (String s : sumField) {
                        ParsedSum sum = (ParsedSum) asMap.get(s);
                        map.put(s, sum.getValueAsString());
                    }
                }
                returnMap.put(key, map);
            });
            return returnMap;
        } catch (Exception e) {
            log.error("com.cloud.baowang.common.es.service.impl.OrderBaseEsServiceImpl.countAndSumGroupByDto :query{} error", queryBuilder, e);
        }
        return null;
    }

    /**
     * @param queryBuilder 条件构造器
     * @param groupByField 需要分组的字段（不同分组结果不同,最少一个分组） ,可传null
     * @param countField   需要count的字段，返回的值为 count_(+传入字段名),可传null
     * @param sumField     需要sum的字段，返回的值为 sum_(+传入字段名),可传null
     * @param avgField     需要avg的字段，返回的值为 avg_(+传入字段名),可传null
     * @param havingField  聚合后的条件结果筛选（例如筛选字段名为id，count结果大于等于5，示例：new String[][]{{"count_id", ">=", "5"}}，数组为二维数组，可传多条件）,可传null
     */
    public Map<String, Map<String, Object>> countAndSumAndAvgGroupHavingByDto(BoolQueryBuilder queryBuilder, String groupByField,
                                                                              List<String> countField, List<String> sumField, List<String> avgField,
                                                                              String[][] havingField) {
        Map<String, Map<String, Object>> returnMap = Maps.newHashMap();
        SearchRequest searchRequest = new SearchRequest(esIndex);
        TermsAggregationBuilder groupByBuilder = AggregationBuilders.terms(groupByField).field(groupByField).size(Integer.MAX_VALUE);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);


        if (countField != null) {
            for (String s : countField) {
                ValueCountAggregationBuilder terms = AggregationBuilders.count("count_" + s).field(s);
                groupByBuilder.subAggregation(terms);
            }
        }
        if (sumField != null) {
            for (String s : sumField) {
                SumAggregationBuilder terms = AggregationBuilders.sum("sum_" + s).field(s);
                groupByBuilder.subAggregation(terms);
            }
        }
        if (avgField != null) {
            for (String s : avgField) {
                AvgAggregationBuilder terms = AggregationBuilders.avg("avg_" + s).field(s);
                groupByBuilder.subAggregation(terms);
            }
        }

        if (havingField != null) {
            Map<String, String> bucketsPathsMap = Maps.newHashMap();
            StringBuilder scriptStr = new StringBuilder();
            for (String[] s : havingField) {
                //声明BucketPath，用于后面的bucket筛选
                if (s[2] != null) {
                    bucketsPathsMap.put(s[0], s[0]);
                    scriptStr.append(" params.").append(s[0]).append(" ").append(s[1]).append(" ").append(s[2]).append(" &&");
                }
            }
            if (!scriptStr.isEmpty()) {
                //设置脚本
                Script script = new Script(scriptStr.substring(0, scriptStr.length() - 2));
                //构建bucket选择器
                BucketSelectorPipelineAggregationBuilder bucketBuilder = PipelineAggregatorBuilders.bucketSelector("having", bucketsPathsMap, script);

                groupByBuilder.subAggregation(bucketBuilder);
            }
        }
        searchSourceBuilder.aggregation(groupByBuilder);

        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            ParsedLongTerms group = (ParsedLongTerms) searchResponse.getAggregations().asMap().get(groupByField);
            group.getBuckets().forEach(bucket -> {
                HashMap<String, Object> map = Maps.newHashMap();
                String key = bucket.getKeyAsString();
                Map<String, Aggregation> asMap = bucket.getAggregations().asMap();
                map.put(groupByField, Long.valueOf(key));

                if (countField != null) {
                    for (String s : countField) {
                        String tempKey = "count_" + s;
                        ParsedValueCount sum = (ParsedValueCount) asMap.get(tempKey);
                        map.put(tempKey, sum.getValue());
                    }
                }
                if (sumField != null) {
                    for (String s : sumField) {
                        String tempKey = "sum_" + s;
                        ParsedSum sum = (ParsedSum) asMap.get(tempKey);
                        map.put(tempKey, sum.getValue());
                    }
                }
                if (avgField != null) {
                    for (String s : avgField) {
                        String tempKey = "avg_" + s;
                        ParsedAvg avg = (ParsedAvg) asMap.get(tempKey);
                        map.put(tempKey, avg.getValue());
                    }
                }
                returnMap.put(key, map);
            });
        } catch (Exception e) {
            log.error("com.cloud.baowang.common.es.service.impl." +
                    "OrderBaseEsServiceImpl.countAndSumAndAvgGroupHavingByDto.param:" +
                    "queryBuilder:{},groupByField:{},countField;{},sumField:{}," +
                    "avgField:{},havingField:{}", queryBuilder, groupByField, countField, sumField, avgField, havingField, e);
        }

        return returnMap;
    }
}
