package com.cloud.baowang.wallet.api.vo.userTypingAmount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetUserTypingAmountVO {
    /**
     * 会员账号
     */
    private List<String> userAccountList;

}
