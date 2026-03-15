package com.cloud.baowang.play.wallet.vo.req.db.evg.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AceltTradeInfo {

    private String member;

    private String balance;

}
