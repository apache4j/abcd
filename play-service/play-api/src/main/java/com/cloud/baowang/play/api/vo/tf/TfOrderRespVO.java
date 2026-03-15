package com.cloud.baowang.play.api.vo.tf;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TfOrderRespVO {

    /**
     * 条数
     */
    @JsonProperty("count")
    private int count;
    /**
     * 下一页
     */
    @JsonProperty("next")
    private String next;
    /**
     * 上一页
     */
    @JsonProperty("previous")
    private String previous;
    /**
     * 结果
     */
    @JsonProperty("results")
    private List<TfOrderInfoVO> results;


}
