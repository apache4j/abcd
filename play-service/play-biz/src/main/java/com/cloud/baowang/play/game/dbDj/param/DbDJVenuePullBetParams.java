/**
 * @(#)WeVenuePullBetParams.java, 9月 15, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.play.game.dbDj.param;

import com.cloud.baowang.play.task.po.VenuePullBetParams;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DbDJVenuePullBetParams extends VenuePullBetParams {

    private Long startTime;

    private Long endTime;

    private Long last_order_id;

    private Long step;
}
