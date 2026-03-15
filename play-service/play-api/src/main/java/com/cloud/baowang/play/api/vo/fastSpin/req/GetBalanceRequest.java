package com.cloud.baowang.play.api.vo.fastSpin.req;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetBalanceRequest {

    String acctId;
    String gameCode;
}
