package com.cloud.baowang.system.api.vo.exchange;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/5/21 15:35
 * @Version: V1.0
 **/
@Data
@Schema(description = "虚拟货币汇率 计算")
public class CalculateRateReqVO {

    @Schema(description = "主键ID")
    @NotNull(message = "id不能为空")
    private String id;

    @Schema(description = "汇率调整方式")
    @NotNull(message = "汇率调整方式不能为空")
    private String adjustWay;

    @Schema(description = "调整数值")
    @NotNull(message = "调整数值不能为空")
    private String adjustNum;

}
