package com.cloud.baowang.report.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * @Author : 小智
 * @Date : 11/11/23 5:45 PM
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ActiveByAgentVO", description = "对有效活跃会员根据代理组装统计")
public class ActiveByAgentVO implements Serializable {
//
//    @Schema(description ="活跃")
//    private Map<String, Set<String>> active;

    @Schema(description ="有效活跃")
    private Map<String, Set<String>> validActive;

//    @Schema(description ="新增")
//    private Map<String, Set<String>> add;

    @Schema(description ="有效新增")
    private Map<String, Set<String>> validAdd;


}
