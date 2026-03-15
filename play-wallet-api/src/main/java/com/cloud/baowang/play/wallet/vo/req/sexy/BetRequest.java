package com.cloud.baowang.play.wallet.vo.req.sexy;

import lombok.Data;

import java.util.List;

@Data
public class BetRequest {
    private List<SexyBetRequest> txns;
}
