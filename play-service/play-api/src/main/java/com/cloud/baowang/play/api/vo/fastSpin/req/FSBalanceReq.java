package com.cloud.baowang.play.api.vo.fastSpin.req;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FSBalanceReq {

    String acctId;
    String merchantCode;

    String body;

}
