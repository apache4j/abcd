package com.cloud.baowang.play.game.shaba;

import com.cloud.baowang.play.game.shaba.response.BetDetail;
import lombok.Data;

import java.util.List;

@Data
public class SBABetRecordData {

    private String last_version_key;

    private List<BetDetail> BetDetails;

    private List<BetDetail> BetVirtualSportDetails;

    private List<BetDetail> BetNumberDetails;



}
