/**
 * @(#)WeVenuePullBetParams.java, 9月 15, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.play.task.pulltask.jdb.vo;

import com.cloud.baowang.play.task.po.VenuePullBetParams;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JdbActionVo  {

    private Integer action;
    private Long ts;
    private String parent;

    private String starttime;

    private String endtime;

    private List<Integer> gTypes;
}
