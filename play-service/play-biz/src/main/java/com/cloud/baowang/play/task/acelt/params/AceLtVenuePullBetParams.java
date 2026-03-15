package com.cloud.baowang.play.task.acelt.params;

import com.cloud.baowang.play.task.po.VenuePullBetParams;
import lombok.Data;

/**
 * <h2></h2>
 */
@Data
public class AceLtVenuePullBetParams extends VenuePullBetParams {

    private Long startTime;
    private Long endTime;
    private Long step;
    private Long lastOrderId;
}
