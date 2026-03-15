package com.cloud.baowang.report.api.vo.vip;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author : 小智
 * @Date : 2024/11/6 20:20
 * @Version : 1.0
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "vip报表查询请求参数")
public class ReportVIPDataReq extends PageVO implements Serializable {

    @Schema(title = "统计日期")
    private Long startTime;

    @Schema(title = "VIP段位")
    private Integer vipRankCode;

    @Schema(title = "VIP等级")
    private Integer vipGradeCode;

    private String siteCode;

    private Integer handicapMode;
}
