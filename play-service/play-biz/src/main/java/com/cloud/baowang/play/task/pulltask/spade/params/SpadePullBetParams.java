package com.cloud.baowang.play.task.pulltask.spade.params;

import com.cloud.baowang.play.task.po.VenuePullBetParams;
import lombok.Data;

@Data
public class SpadePullBetParams extends VenuePullBetParams {
    private Long startTime;

    private Long endTime;

    private Long step = 600000L;
}
