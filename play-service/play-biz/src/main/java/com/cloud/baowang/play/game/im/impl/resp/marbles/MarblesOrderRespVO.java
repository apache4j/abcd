package com.cloud.baowang.play.game.im.impl.resp.marbles;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarblesOrderRespVO {
    @JsonProperty("Code")
    private Integer code;

    @JsonProperty("Result")
    private List<MarblesOrderDataRespVO> result;

    @JsonProperty("Message")
    private String message;

    @JsonProperty("TotalCount")
    private Integer totalCount;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Pagination{

        @JsonProperty("CurrentPage")
        private Integer currentPage;

        @JsonProperty("TotalPage")
        private Integer totalPage;

        @JsonProperty("ItemPerPage")
        private Integer itemPerPage;

        @JsonProperty("TotalCount")
        private Integer totalCount;
    }

}
