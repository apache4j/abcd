package com.cloud.baowang.play.game.lgd.respon;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class LGDContentResp {
    @JsonProperty("RESPONSECODE")
    private String responseCode;

    @JsonProperty("START_TIME")
    private String startTime;

    @JsonProperty("END_TIME")
    private String endTime;
    @JsonProperty("PAGENUMBER")
    private Integer pageNumber;

    @JsonProperty("BETSDETAILS")
    private List<DetsDetailsResp> betsDetails;

}
