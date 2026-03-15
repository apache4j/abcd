package com.cloud.baowang.agent.api.vo.commission;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/10/22 22:39
 * @description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActiveNumberPageReqVO extends PageVO {
    @Schema( description = "代理Id")
    private String agentId;

    @Schema( description = "开始时间")
    private Long startTime;

    @Schema( description = "结束时间")
    private Long endTime;
}
