package com.cloud.baowang.play.game.acelt.response;

import lombok.Data;

import java.util.List;

/**
 * <h2></h2>
 */
@Data
public class AceLtPageRes<R> {
    /**
     * 总条数
     */
    private Long total;
    /**
     * 页数
     */
    private Integer current;
    /**
     * 数据
     */
    private List<R> records;

    /**
     * 页数
     */
    private Integer pages;
}
