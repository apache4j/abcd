package com.cloud.baowang.play.api.vo.sa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SAGetUserBalanceReq {

    /**
     * 用户名，最长48字符，必填
     */
    private String username;

    /**
     * 货币单位（标准 ISO 3 字符，如 USD、EUR，或特例如 mXBT），最长16字符，必填
     */
    private String currency;

}
