package com.cloud.baowang.play.wallet.vo.req.db.sh.vo;

import lombok.Data;
import java.math.BigInteger;

@Data
public class BetRequestVO {
    private String merchantCode;
    private String signature;
    private Long transferNo;
    private String params;
    private Long timestamp;
}
