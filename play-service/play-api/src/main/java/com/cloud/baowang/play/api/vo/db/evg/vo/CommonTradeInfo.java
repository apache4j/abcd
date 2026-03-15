package com.cloud.baowang.play.api.vo.db.evg.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonTradeInfo {
    private Integer tradeType;
    private Long tradeAmount;
    private Long balance;

    //0:失败,1:成功
    private Integer status;

    private String memberId;

    private String tradeId;

    private String orderId;

    private List<AceltTradeInfo> aceltTradeInfo;
}
