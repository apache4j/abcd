package com.cloud.baowang.play.api.vo.db.sh.vo;

import lombok.Data;

@Data
public class BetRequestVO {
    private String merchantCode;
    private String signature;
    private Long transferNo;
    private String params;
    private Long timestamp;
}
