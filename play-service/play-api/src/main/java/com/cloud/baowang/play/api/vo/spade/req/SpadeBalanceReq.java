package com.cloud.baowang.play.api.vo.spade.req;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpadeBalanceReq {
    String acctId;
    String gameCode;
    String serialNo;
    String merchantCode;
    String body;

    String digest;
}
