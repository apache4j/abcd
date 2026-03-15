package com.cloud.baowang.report.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author : 小智
 * @Date : 11/11/23 5:45 PM
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "ReportAgentActiveVO", description = "代理活跃和有效活跃满足的会员")
public class ReportAgentActiveVO implements Serializable {

    @Schema(description = "今日活跃")
    private List<String> todayActive;

    @Schema(description = "今日有效活跃")
    private List<String> todayValidActive;

    @Schema(description = "本月活跃")
    private List<String> monthActive;

    @Schema(description = "本月有效活跃")
    private List<String> monthValidActive;
}
