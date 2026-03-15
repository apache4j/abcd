package com.cloud.baowang.user.api.vo.user.reponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserVIPFlowRecordVO {

    /* 会员账号 */
    private String userAccount;

    /* vip等级code */
    private Integer vipRankCode;

    /* VIP升降级标识(0:升级,1:降级,2:保级) */
    private String status;

    /* 单次有效流水金额 */
    private BigDecimal validAmount;

    /* 该会员累计有效流水 */
    private BigDecimal validSumAmount;

    private String lastVipTime;

    public BigDecimal getValidAmount() {
        return Optional.ofNullable(validAmount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getValidSumAmount() {
        return Optional.ofNullable(validSumAmount).orElse(BigDecimal.ZERO);
    }

}
