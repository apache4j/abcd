package com.cloud.baowang.play.api.vo.sexy.req;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SexyGameInfo {
    private List<String> result;
    private String roundStartTime;
    private String winner;
    private String ip;
    private Double odds;
    private String tableId;
    private String dealerDomain;
    private BigDecimal winLoss;
    private String status;
}
