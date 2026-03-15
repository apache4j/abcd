package com.cloud.baowang.play.api.vo.sexy.req;

import lombok.Data;

import java.util.List;

@Data
public class BetRequest {
    private List<SexyBetRequest> txns;
}
