package com.cloud.baowang.system.api.vo.risk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "多个风控账号查询对象")
public class RiskQueryVO implements Serializable {
    @Schema(title = "账号")
    private String riskControlAccount;
    @Schema(title = "风控类型code")
    private String riskControlTypeCode;
    @Schema(title = "id优先")
    private Long id;
}
