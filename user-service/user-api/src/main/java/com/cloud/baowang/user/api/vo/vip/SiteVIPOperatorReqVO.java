package com.cloud.baowang.user.api.vo.vip;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author : 小智
 * @Date : 2024/8/13 13:50
 * @Version : 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "站点后台VIP权益配置返回对象")
public class SiteVIPOperatorReqVO extends PageVO {

    @Schema(description = "操作类型")
    private String operatorType;

    @Schema(description = "操作项")
    private List<String> operatorItem;

    @Schema(description = "操作人")
    private String operator;

    @Schema(description = "开始时间")
    private Long beginTime;

    @Schema(description = "结束时间")
    private Long endTime;
}
