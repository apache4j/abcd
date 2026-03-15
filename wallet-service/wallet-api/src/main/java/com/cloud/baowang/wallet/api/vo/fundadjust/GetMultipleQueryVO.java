package com.cloud.baowang.wallet.api.vo.fundadjust;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: kimi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title ="查询流水倍数 Request")
public class GetMultipleQueryVO {

    @Schema(title ="活动类型code")
    @NotNull(message = "活动类型code不能为空")
    private Integer activityTypeCode;

    @Schema(title ="活动ID  如果是首存活动/充值送活动，需要传递活动ID")
    private String activityId;

    @Schema(title ="会员账号  如果是VIP晋级优惠，需要传递会员账号")
    private String userAccount;
}
