package com.cloud.baowang.play.api.vo.fastSpin.res;

import com.cloud.baowang.play.api.vo.fastSpin.AcctInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class FSBalanceRes extends FSBaseRes{

    String merchantCode;
    String serialNo;

    AcctInfo acctInfo;
}
