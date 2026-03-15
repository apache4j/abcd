package com.cloud.baowang.play.wallet.vo.req.db.evg.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DBEVGBasicInfo {
    private String agent;
    private long timestamp;
    private long randno;
    private String sign;
}
