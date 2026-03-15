package com.cloud.baowang.agent.api.vo.commission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: fangfei
 * @createTime: 2024/10/21 11:54
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "代理本月佣金比例查询对象")
public class CommissionExpectReqVO {
    private String agentId;
    private String siteCode;
}
