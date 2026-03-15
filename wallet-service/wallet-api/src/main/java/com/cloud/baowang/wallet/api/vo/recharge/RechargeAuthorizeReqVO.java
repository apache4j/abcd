package com.cloud.baowang.wallet.api.vo.recharge;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author : 小智
 * @Date : 2024/7/30 10:42
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="存款授权请求入参")
public class RechargeAuthorizeReqVO extends PageVO {

    @Schema(description ="币种")
    private String currency;

    @Schema(description = "站点code")
    private String siteCode;

    @Schema(description ="充值类型")
    private String rechargeTypeId;

    @Schema(description ="充值方式")
    private String rechargeWayId;

    @Schema(description ="状态")
    private String status;
}
