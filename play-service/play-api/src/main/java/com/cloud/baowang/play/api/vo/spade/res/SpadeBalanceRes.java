package com.cloud.baowang.play.api.vo.spade.res;

import com.cloud.baowang.play.api.vo.spade.SpadeAcctInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class SpadeBalanceRes extends SpadeBaseRes {

    String merchantCode;
    String serialNo;

    SpadeAcctInfo acctInfo;
}
