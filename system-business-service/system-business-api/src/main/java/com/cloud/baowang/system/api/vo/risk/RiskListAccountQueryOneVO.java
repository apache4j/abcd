package com.cloud.baowang.system.api.vo.risk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author : 小智
 * @Date : 25/5/23 11:11 AM
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "风控账号查询单个个对象")
public class RiskListAccountQueryOneVO implements Serializable {

    @Schema(title = "账号")
    private String riskControlAccount;
    @Schema(title = "风控类型code")
    private List<String>riskControlTypeCode;
}
