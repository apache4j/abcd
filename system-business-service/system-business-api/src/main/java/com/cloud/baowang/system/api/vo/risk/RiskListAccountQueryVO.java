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
@Schema(title = "风控账号查询多个对象")
public class RiskListAccountQueryVO implements Serializable {

    @Schema(title = "账号")
    private List<String> riskControlAccounts;

    /**
     * RiskTypeEnum
     * {@link com.cloud.baowang.common.core.enums.RiskTypeEnum}
     */
    @Schema(title = "风控类型code")
    private String riskControlTypeCode;

    @Schema(title = "站点code")
    private String siteCode;
}
