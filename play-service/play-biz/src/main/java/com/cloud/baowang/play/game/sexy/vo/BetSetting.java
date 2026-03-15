package com.cloud.baowang.play.game.sexy.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
public class BetSetting {
    private String venueCode;
    private String gameType;
    private List<Long> limitId;

    public BetSetting(String venueCode, String gameType, List<Long> limitId) {
        this.venueCode = venueCode;
        this.gameType = gameType;
        this.limitId = limitId;
    }

}

