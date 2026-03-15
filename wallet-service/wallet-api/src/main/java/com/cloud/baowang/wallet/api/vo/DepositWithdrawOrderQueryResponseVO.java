package com.cloud.baowang.wallet.api.vo;

import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.OwnerUserTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="存提订单查询返回对象")
public class DepositWithdrawOrderQueryResponseVO {



    /**
     * {@link OwnerUserTypeEnum}
     */
    @Schema(description = "订单用户类型  ")
    private String ownerUserType;

    /**
     * {@link DepositWithdrawalOrderTypeEnum}
     */
    @Schema(description = "订单类型  ")
    private Integer orderType;
}
