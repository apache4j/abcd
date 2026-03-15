package com.cloud.baowang.user.api.vo.vip;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author 小智
 * @Date 6/5/23 1:56 PM
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@Schema(title = "VIP配置操作记录返回对象")
public class VIPOperationVO implements Serializable {

    @Schema(title = "操作类型code")
    private String operationType;

    @Schema(title = "操作类型")
    private String operationTypeName;

    @Schema(title = "操作项code")
    private String operation;

    @Schema(title = "操作项")
    private String operationName;

    @Schema(title = "操作前")
    private String operationBefore;

    @Schema(title = "操作后")
    private String operationAfter;

    @Schema(title = "操作人")
    private String operator;

    @Schema(title = "操作时间")
    private Long operationTime;
}
