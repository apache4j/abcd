package com.cloud.baowang.play.wallet.vo.req.db.acelt.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class TransferRspData {
    private String code;

    private String msg;

    private String serverTime;
}
