package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author qiqi
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "状态修改对象")
public class BatchGameClassStatusRequestUpVO {

    @Schema(description = "ID", required = true)
    private String id;


    @Schema(description = "批量修改ID", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<String> idBatch;


    @Schema(description = "ID", required = true)
    private List<String> ids;

    @Schema(description = "状态 1.开启中 2.维护中 3.已禁用 字典code:platform_class_status_type", required = true)
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer status;

    @Schema(description = "维护时间开始")
    private Long maintenanceStartTime;

    @Schema(description = "维护时间结束")
    private Long maintenanceEndTime;

    @Schema(description = "备注")
    private String remark;

    private String updater;

    @Schema(description = "场馆",hidden = true)
    private String venueCode;



}
