package com.cloud.baowang.user.api.vo.vip;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author 小智
 * @Date 6/5/23 1:56 PM
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "VIP操作配置入参对象")
public class VIPOperationRequestVO extends PageVO implements Serializable {

    @Schema(title = "操作类型")
    private String operationType;

    @Schema(title = "操作项")
    private List<String> operation;

    @Schema(title = "操作人")
    private String operator;

    @Schema(title = "操作开始时间")
    private Long operationStartTime;

    @Schema(title = "操作结束时间")
    private Long operationEndTime;
}
