package com.cloud.baowang.user.api.vo.vip;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author : 小智
 * @Date : 2024/8/13 11:56
 * @Version : 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "站点后台VIP权益配置返回对象")
public class SiteVIPBenefitOperationVO {

    @Schema(description = "操作时间")
    private Long operatorTime;

    @Schema(description = "操作类型")
    private String operationType;

    @Schema(description = "调整等级")
    private String adjustLevel;

    @Schema(description = "操作项")
    private String operationItem;

    @Schema(description = "变更前")
    private String changeBefore;

    @Schema(description = "变更后")
    private String changeAfter;

    @Schema(title = "操作人")
    private String operator;
}
