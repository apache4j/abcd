/**
 * @(#)WeVenuePullBetParams.java, 9月 15, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.play.task.pulltask.sexy;

import com.cloud.baowang.play.task.po.VenuePullBetParams;
import lombok.*;

/**
 *
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class SexyVenuePullBetParams extends VenuePullBetParams {

    private Long startTime;
    private Long endTime;

    private Long step;

    private String versionKey;
}
