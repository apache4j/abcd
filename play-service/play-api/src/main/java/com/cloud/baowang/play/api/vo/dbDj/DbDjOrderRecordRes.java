package com.cloud.baowang.play.api.vo.dbDj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DbDjOrderRecordRes {
    /**
     * 请求状态, 旧接口为 state, v2 接口为 status
     */
    private String state;

    /**
     * 返回数据
     */
    private String data;

    /**
     * 最后一条注单ID
     */
    private Long last_order_id;

    /**
     * 页数大小
     */
    private Integer page_size;

    /**
     * 数据总数
     */
    private Integer total;

    /**
     * 数据对象
     */
    private List<DbDJOrderRecordBetRes> bet;

    /**
     * 联赛名称对象
     */
    private Map<String, Map<String, String>> tournament;

    /**
     * 联赛名称对象(英文)
     */
    private Map<String, Map<String, String>> tournament_en;


}
