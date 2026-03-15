package com.cloud.baowang.play.task.pulltask.jl.params;

import com.cloud.baowang.play.task.po.VenuePullBetParams;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import lombok.Data;

@Data
public class JILIPullBetParams extends VenuePullBetParams {
    private Long startTime;

    private Long endTime;

    private Long step = 600000L;
}
