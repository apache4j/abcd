package com.cloud.baowang.system.api.vo.exchange;

import com.cloud.baowang.common.core.vo.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/5/20 20:50
 * @Version: V1.0
 **/
@Data
@Schema(description = "虚拟货币汇率 修改")
public class RateEditReqVO extends BaseVO {

    @Schema(description = "主键ID")
    @NotNull(message = "id不能为空")
    private String id;
    @Schema(description = "站点编号",hidden = true)
    private String siteCode;

    @Schema(description = "汇率调整方式")
    @NotNull(message = "汇率调整方式不能为空")
    private String adjustWay;

    @Schema(description = "调整数值")
    @NotNull(message = "调整数值不能为空")
    private String adjustNum;

}
