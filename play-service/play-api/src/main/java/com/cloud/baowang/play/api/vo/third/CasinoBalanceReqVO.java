package com.cloud.baowang.play.api.vo.third;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CasinoBalanceReqVO implements Serializable {
    /**
     * 账户名
     */
    private String userAccount;
    /**
     * 娱乐城名称
     */
    private String venueCode;
}
