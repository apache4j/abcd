package com.cloud.baowang.play.api.vo.fc.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetBalanceReq {
    String MemberAccount;
    String Currency;
    Integer GameID;
    Long Ts;
}
