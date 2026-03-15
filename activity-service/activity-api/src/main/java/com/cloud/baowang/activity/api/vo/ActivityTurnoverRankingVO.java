package com.cloud.baowang.activity.api.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 每日竞赛
 */
@Data
public class ActivityTurnoverRankingVO extends ActivityBaseVO implements Serializable {


    public boolean validateActivityTurnoverRanking() {
        return false;
    }
}
