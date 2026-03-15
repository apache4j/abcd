package com.cloud.baowang.wallet.api.vo.fundadjust;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDepositAmountReqVO {

    private List<String> userIds;

    private Long startTime;

    private Long endTime;

}
